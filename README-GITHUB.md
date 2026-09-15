# ساخت APK با GitHub Actions

## خیلی مهم
تمام **محتویات همین پوشه** را داخل ریشه Repository گیت‌هاب قرار بده؛ نباید خود پوشه `AttendanceAppComplete` را یک سطح دیگر داخل Repository قرار بدهی.

در ریشه Repository باید این موارد را ببینی:

- `.github/workflows/build-apk.yml`
- `app/`
- `build.gradle.kts`
- `settings.gradle.kts`
- `gradle.properties`

## ساخت APK
1. Repository را روی GitHub بساز.
2. فایل‌های بالا را در ریشه Repository آپلود کن.
3. روی `Commit changes` بزن.
4. از منوی `Actions` اجرای `Build Android APK` را باز کن.
5. بعد از سبز شدن Build، وارد همان اجرای Workflow شو.
6. در بخش `Artifacts`، فایل `AttendanceApp-debug-apk` را دانلود کن.
7. فایل ZIP دانلودشده را باز کن؛ داخل آن APK قرار دارد.

Workflow با JDK 17 و Gradle 9.6 پروژه را Build می‌کند. این ترکیب با AGP 9.4 سازگار است.
