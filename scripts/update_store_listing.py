#!/usr/bin/env python3
"""Update Google Play Store listing with translations for all languages."""

import json
import sys
from google.oauth2 import service_account
from googleapiclient.discovery import build

SERVICE_ACCOUNT_FILE = "/Users/up/Downloads/api-4885774509106726948-839507-6792ab008df8.json"
PACKAGE_NAME = "com.perfectappstudio.scientificcalc"

SCOPES = ["https://www.googleapis.com/auth/androidpublisher"]

# App name translations - "Scientific Calculator" in each language
APP_NAMES = {
    "af": "Wetenskaplike Sakrekenaar",
    "am": "ሳይንሳዊ ካልኩሌተር",
    "ar": "آلة حاسبة علمية",
    "hy-AM": "Գիտական Հաշdelays",
    "az-AZ": "Elmi Kalkulyator",
    "bn-BD": "বৈজ্ঞানিক ক্যালকুলেটর",
    "be": "Навуковы Калькулятар",
    "bg": "Научен Калкулатор",
    "my-MM": "သိပ္ပံ ဂဏန်းတွက်စက်",
    "ca": "Calculadora Científica",
    "zh-HK": "科學計算器",
    "zh-CN": "科学计算器",
    "zh-TW": "科學計算機",
    "hr": "Znanstveni Kalkulator",
    "cs-CZ": "Vědecká Kalkulačka",
    "da-DK": "Videnskabelig Lommeregner",
    "nl-NL": "Wetenschappelijke Rekenmachine",
    "en-AU": "Scientific Calculator",
    "en-CA": "Scientific Calculator",
    "en-IN": "Scientific Calculator",
    "en-SG": "Scientific Calculator",
    "en-ZA": "Scientific Calculator",
    "en-GB": "Scientific Calculator",
    "en-US": "Scientific Calculator",
    "et": "Teaduslik Kalkulaator",
    "fil": "Scientific Calculator",
    "fi-FI": "Tieteellinen Laskin",
    "fr-FR": "Calculatrice Scientifique",
    "fr-CA": "Calculatrice Scientifique",
    "gl-ES": "Calculadora Científica",
    "ka-GE": "სამეცნიერო კალკულატორი",
    "de-DE": "Wissenschaftlicher Taschenrechner",
    "el-GR": "Επιστημονική Αριθμομηχανή",
    "gu": "વૈજ્ઞાનિક કેલ્ક્યુલેટર",
    "iw-IL": "מחשבון מדעי",
    "hi-IN": "वैज्ञानिक कैलकुलेटर",
    "hu-HU": "Tudományos Számológép",
    "is-IS": "Vísindareiknir",
    "id": "Kalkulator Ilmiah",
    "it-IT": "Calcolatrice Scientifica",
    "ja-JP": "関数電卓",
    "kn-IN": "ವೈಜ್ಞಾನಿಕ ಕ್ಯಾಲ್ಕುಲೇಟರ್",
    "kk": "Ғылыми Калькулятор",
    "km-KH": "ម៉ាស៊ីនគិតលេខវិទ្យាសាស្ត្រ",
    "ko-KR": "공학용 계산기",
    "ky-KG": "Илимий Калькулятор",
    "lo-LA": "ເຄື່ອງຄິດໄລ່ວິທະຍາສາດ",
    "lv": "Zinātniskais Kalkulators",
    "lt": "Mokslinis Skaičiuotuvas",
    "mk-MK": "Научен Калкулатор",
    "ms": "Kalkulator Saintifik",
    "ms-MY": "Kalkulator Saintifik",
    "ml-IN": "ശാസ്ത്രീയ കാൽക്കുലേറ്റർ",
    "mr-IN": "वैज्ञानिक कॅल्क्युलेटर",
    "mn-MN": "Шинжлэх Ухааны Тооцоолуур",
    "ne-NP": "वैज्ञानिक क्याल्कुलेटर",
    "no-NO": "Vitenskapelig Kalkulator",
    "fa": "ماشین حساب علمی",
    "pl-PL": "Kalkulator Naukowy",
    "pt-BR": "Calculadora Científica",
    "pt-PT": "Calculadora Científica",
    "pa": "ਵਿਗਿਆਨਕ ਕੈਲਕੁਲੇਟਰ",
    "ro": "Calculator Științific",
    "rm": "Calculatur Scientific",
    "ru-RU": "Научный Калькулятор",
    "sr": "Научни Калкулатор",
    "si-LK": "විද්‍යාත්මක කැල්කියුලේටරය",
    "sk": "Vedecká Kalkulačka",
    "sl": "Znanstveni Kalkulator",
    "es-419": "Calculadora Científica",
    "es-ES": "Calculadora Científica",
    "es-US": "Calculadora Científica",
    "sw": "Kikokotoo cha Kisayansi",
    "sv-SE": "Vetenskaplig Kalkylator",
    "ta-IN": "அறிவியல் கால்குலேட்டர்",
    "te-IN": "శాస్త్రీయ కాలిక్యులేటర్",
    "th": "เครื่องคิดเลขวิทยาศาสตร์",
    "tr-TR": "Bilimsel Hesap Makinesi",
    "uk": "Науковий Калькулятор",
    "ur": "سائنسی کیلکولیٹر",
    "vi": "Máy Tính Khoa Học",
    "zu": "Isibali Sesayensi",
}

# Short description translations
SHORT_DESC = {
    "en-US": "Advanced scientific calculator with graphing, solver, and unit converter",
    "en-GB": "Advanced scientific calculator with graphing, solver, and unit converter",
    "ja-JP": "グラフ、方程式ソルバー、単位変換機能付き高機能関数電卓",
    "ko-KR": "그래프, 방정식 풀이, 단위 변환 기능을 갖춘 고급 공학용 계산기",
    "zh-CN": "带图形绘制、方程求解和单位转换的高级科学计算器",
    "zh-TW": "具備繪圖、方程式求解和單位轉換功能的進階科學計算機",
    "es-ES": "Calculadora científica avanzada con gráficos, solucionador y conversor",
    "es-419": "Calculadora científica avanzada con gráficos, solucionador y conversor",
    "fr-FR": "Calculatrice scientifique avancée avec graphiques, solveur et convertisseur",
    "de-DE": "Wissenschaftlicher Taschenrechner mit Grafik, Gleichungslöser und Umrechner",
    "it-IT": "Calcolatrice scientifica avanzata con grafici, risolutore e convertitore",
    "pt-BR": "Calculadora científica avançada com gráficos, resolvedor e conversor",
    "ru-RU": "Научный калькулятор с графиками, решателем уравнений и конвертером",
    "hi-IN": "ग्राफ, समीकरण हल और इकाई कनवर्टर के साथ उन्नत वैज्ञानिक कैलकुलेटर",
    "ar": "آلة حاسبة علمية متقدمة مع رسوم بيانية وحل المعادلات ومحول الوحدات",
    "th": "เครื่องคิดเลขวิทยาศาสตร์ขั้นสูงพร้อมกราฟ ตัวแก้สมการ และตัวแปลงหน่วย",
    "vi": "Máy tính khoa học nâng cao với đồ thị, giải phương trình và chuyển đổi đơn vị",
    "id": "Kalkulator ilmiah canggih dengan grafik, pemecah persamaan, dan konversi satuan",
    "ms": "Kalkulator saintifik lanjutan dengan graf, penyelesai dan penukar unit",
    "tr-TR": "Grafik, denklem çözücü ve birim dönüştürücülü gelişmiş bilimsel hesap makinesi",
    "pl-PL": "Zaawansowany kalkulator naukowy z wykresami, solwerem i konwerterem jednostek",
    "nl-NL": "Geavanceerde wetenschappelijke rekenmachine met grafieken, oplosser en omrekener",
    "uk": "Науковий калькулятор з графіками, розв'язувачем рівнянь та конвертером одиниць",
    "sv-SE": "Avancerad vetenskaplig kalkylator med grafer, ekvationslösare och enhetsomvandlare",
    "da-DK": "Avanceret videnskabelig lommeregner med grafer, ligningsløser og enhedsomregner",
    "fi-FI": "Edistynyt tieteellinen laskin kaavioilla, yhtälönratkaisijalla ja yksikkömuuntimella",
    "no-NO": "Avansert vitenskapelig kalkulator med grafer, ligningsløser og enhetsomregner",
    "cs-CZ": "Pokročilá vědecká kalkulačka s grafy, řešičem rovnic a převodníkem jednotek",
    "ro": "Calculator științific avansat cu grafice, rezolvitor și convertor de unități",
    "hu-HU": "Fejlett tudományos számológép grafikonokkal, egyenletmegoldóval és mértékegység-váltóval",
    "bg": "Научен калкулатор с графики, решаване на уравнения и конвертор на единици",
    "el-GR": "Προηγμένη επιστημονική αριθμομηχανή με γραφικά, επίλυση εξισώσεων και μετατροπέα",
    "fa": "ماشین حساب علمی پیشرفته با نمودار، حل معادلات و تبدیل واحد",
    "bn-BD": "গ্রাফ, সমীকরণ সমাধান এবং একক রূপান্তরকারী সহ উন্নত বৈজ্ঞানিক ক্যালকুলেটর",
    "ta-IN": "வரைபடம், சமன்பாடு தீர்வு மற்றும் அலகு மாற்றி கொண்ட மேம்பட்ட அறிவியல் கால்குலேட்டர்",
}

FULL_DESC_EN = """The most powerful scientific calculator on Android — completely free.

Whether you're a student, engineer, or math enthusiast, this calculator delivers Casio fx-991MS level functionality in a beautiful, modern interface.

KEY FEATURES:

🔬 Scientific Calculator
• Trigonometric, logarithmic, exponential functions
• Permutations, combinations, factorials
• Fractions, percentages, degree-minute-second
• Memory variables (A-F, M, Ans)
• Multi-statement calculations with colon separator

📊 Graphing
• Plot multiple functions simultaneously
• Interactive trace mode with coordinates
• Pinch to zoom, pan to explore
• Auto-scale and manual range control

🧮 Equation Solver
• Linear, quadratic, and cubic equations
• 2x2 and 3x3 systems of linear equations
• Newton's method for numerical solutions

🔄 Unit Converter
• Length, weight, temperature, speed, area, volume
• Time, pressure, energy, power, data storage
• Instant conversion between 100+ units

📈 Statistics Mode
• Single and paired variable statistics
• Mean, standard deviation, regression
• Sum, variance, correlation coefficient

🔢 Base-N Calculator
• Binary, octal, decimal, hexadecimal
• Logical operations: AND, OR, XOR, NOT

⚡ Additional Features
• Calculation history with search
• Physical constants library (40+ constants)
• Dark theme optimized for AMOLED
• Works completely offline
• No unnecessary permissions

Built for speed and accuracy. No ads interrupting your workflow."""

def get_default_short_desc(lang):
    """Return short description, falling back to English."""
    return SHORT_DESC.get(lang, SHORT_DESC["en-US"])

def main():
    credentials = service_account.Credentials.from_service_account_file(
        SERVICE_ACCOUNT_FILE, scopes=SCOPES
    )
    service = build("androidpublisher", "v3", credentials=credentials)

    # Create a new edit
    edit = service.edits().insert(
        packageName=PACKAGE_NAME, body={}
    ).execute()
    edit_id = edit["id"]
    print(f"Created edit: {edit_id}")

    # Get current listings to see what languages exist
    try:
        listings = service.edits().listings().list(
            packageName=PACKAGE_NAME, editId=edit_id
        ).execute()
        existing_langs = [l["language"] for l in listings.get("listings", [])]
        print(f"Existing languages: {len(existing_langs)}")
    except Exception as e:
        print(f"Error listing: {e}")
        existing_langs = ["en-US"]

    # Update all languages
    all_langs = set(list(APP_NAMES.keys()) + existing_langs)
    success_count = 0
    fail_count = 0

    for lang in sorted(all_langs):
        title = APP_NAMES.get(lang, "Scientific Calculator")
        short_desc = get_default_short_desc(lang)
        full_desc = FULL_DESC_EN  # Use English for full desc (Play auto-translates)

        # Ensure title is max 30 chars
        if len(title) > 30:
            title = title[:30]

        # Short desc max 80 chars
        if len(short_desc) > 80:
            short_desc = short_desc[:80]

        body = {
            "language": lang,
            "title": title,
            "shortDescription": short_desc,
            "fullDescription": full_desc,
        }

        try:
            service.edits().listings().update(
                packageName=PACKAGE_NAME,
                editId=edit_id,
                language=lang,
                body=body,
            ).execute()
            print(f"  ✓ {lang}: {title}")
            success_count += 1
        except Exception as e:
            print(f"  ✗ {lang}: {e}")
            fail_count += 1

    print(f"\nUpdated {success_count} languages, {fail_count} failures")

    # Commit the edit
    try:
        service.edits().commit(
            packageName=PACKAGE_NAME, editId=edit_id
        ).execute()
        print("✓ Edit committed successfully!")
    except Exception as e:
        print(f"✗ Commit failed: {e}")

if __name__ == "__main__":
    main()
