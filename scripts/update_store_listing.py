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
    edits.commit(packageName=PACKAGE_NAME, editId=edit_id).execute()
    print("Store assets submitted to Google Play's automatic review.")


if __name__ == "__main__":
    main()
