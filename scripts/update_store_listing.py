#!/usr/bin/env python3
"""Prepare reviewed store assets; --apply submits them to Google Play review."""

import argparse
import json
import os
from pathlib import Path

PACKAGE_NAME = "com.perfectappstudio.scientificcalc"
ASSETS = Path(__file__).resolve().parents[1] / "store-assets"


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--apply", action="store_true")
    parser.add_argument("--production-version", type=int)
    args = parser.parse_args()
    title_line, short_line, full_description = (ASSETS / "listing-en-US.txt").read_text().split("\n", 2)
    title = title_line.removeprefix("Title: ").strip()
    short_description = short_line.removeprefix("Short description: ").strip()
    full_description = full_description.strip()
    if not (0 < len(title) <= 30 and 0 < len(short_description) <= 80 and 0 < len(full_description) <= 4000):
        raise ValueError("Store text exceeds Google Play's limits")
    screenshots = sorted((ASSETS / "screenshots/en-US").glob("*.png"))
    if len(screenshots) != 4:
        raise ValueError("Four verified release screenshots are required")
    print(json.dumps({"package": PACKAGE_NAME, "title": title, "shortDescription": short_description,
                      "fullDescription": full_description, "screenshots": [p.name for p in screenshots]}, indent=2))
    if not args.apply:
        return

    from google.oauth2 import service_account
    from googleapiclient.discovery import build
    from googleapiclient.http import MediaFileUpload

    credentials = service_account.Credentials.from_service_account_info(
        json.loads(os.environ["PLAY_SERVICE_ACCOUNT_JSON"]),
        scopes=["https://www.googleapis.com/auth/androidpublisher"],
    )
    service = build("androidpublisher", "v3", credentials=credentials, cache_discovery=False)
    edits = service.edits()
    edit_id = edits.insert(packageName=PACKAGE_NAME, body={}).execute()["id"]
    production_release = None
    if args.production_version is not None:
        properties = dict(line.split("=", 1) for line in (ASSETS.parent / "version.properties").read_text().splitlines())
        if args.production_version != int(properties["VERSION_CODE"]):
            raise ValueError("Production version must match the verified source version")
        internal = edits.tracks().get(packageName=PACKAGE_NAME, editId=edit_id, track="internal").execute()
        internal_codes = {int(code) for release in internal.get("releases", []) for code in release.get("versionCodes", [])}
        if args.production_version not in internal_codes:
            raise ValueError("Requested production version is not available in internal testing")
        production = edits.tracks().get(packageName=PACKAGE_NAME, editId=edit_id, track="production").execute()
        print(json.dumps({"currentProduction": production, "requestedVersion": args.production_version}, indent=2))
        releases = production.get("releases", [])
        if len(releases) != 1 or releases[0].get("status") != "completed":
            raise ValueError("An unexpected production release is active; inspect Play Console")
        old_codes = [int(code) for code in releases[0].get("versionCodes", [])]
        if str(args.production_version) not in releases[0].get("versionCodes", []):
            if not old_codes or args.production_version <= max(old_codes):
                raise ValueError("Requested version is not newer than production")
            production_release = {
                "name": properties["VERSION_NAME"], "status": "completed",
                "versionCodes": [str(code) for code in old_codes if code != max(old_codes)] + [str(args.production_version)],
                "releaseNotes": [{"language": "en-US", "text": "Numerical calculus, searchable constants, decimal/fraction results, examples and help, graph range settings, clearer layouts, calculation fixes, and advertising privacy controls."}],
            }
    listings = edits.listings().list(packageName=PACKAGE_NAME, editId=edit_id).execute().get("listings", [])
    if not any(item["language"] == "en-US" for item in listings):
        raise ValueError("The existing English listing was not found")
    for item in listings:
        language = item["language"]
        if language.startswith("en-") or item.get("fullDescription", "").startswith("The most powerful scientific calculator"):
            body = {key: item[key] for key in ("language", "title", "shortDescription", "fullDescription", "video") if key in item}
            body["fullDescription"] = full_description
            if language.startswith("en-"):
                body["shortDescription"] = short_description
            if language == "en-US":
                body["title"] = title
            edits.listings().update(packageName=PACKAGE_NAME, editId=edit_id, language=language, body=body).execute()
            print(f"Prepared listing: {language}")
    images = edits.images()
    for image_type, paths in (
        ("icon", [ASSETS / "icon-512.png"]),
        ("featureGraphic", [ASSETS / "feature-graphic-1024x500.png"]),
        ("phoneScreenshots", screenshots),
    ):
        images.deleteall(packageName=PACKAGE_NAME, editId=edit_id, language="en-US", imageType=image_type).execute()
        for path in paths:
            images.upload(packageName=PACKAGE_NAME, editId=edit_id, language="en-US", imageType=image_type,
                          media_body=MediaFileUpload(str(path), mimetype="image/png")).execute()
    edits.validate(packageName=PACKAGE_NAME, editId=edit_id).execute()
    if production_release is not None:
        edits.tracks().update(packageName=PACKAGE_NAME, editId=edit_id, track="production",
                              body={"track": "production", "releases": [production_release]}).execute()
        edits.validate(packageName=PACKAGE_NAME, editId=edit_id).execute()
    edits.commit(packageName=PACKAGE_NAME, editId=edit_id).execute()
    print("Requested release and store assets submitted to Google Play's automatic review.")


if __name__ == "__main__":
    main()
