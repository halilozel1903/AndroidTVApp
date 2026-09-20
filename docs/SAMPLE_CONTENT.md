# Sample browse content

Browse rows, posters, seasons, and related titles come from [`app/src/main/assets/catalog.json`](../app/src/main/assets/catalog.json).

## Offline posters

Each entry may define `poster.assetPath` (for example `posters/sherlock.png`) under `app/src/main/assets/`. At runtime the app loads:

`file:///android_asset/<assetPath>`

Bundled PNGs ship with the repo so the browse UI works without network access.

## Network fallback

When an asset file is missing, `MovieCatalog` uses the matching `fallbackUrl` from the catalog. Fallback URLs point at Google’s public Android TV sample bucket (`commondatastorage.googleapis.com/android-tv/Sample%20videos/...`), which is suitable for demos when you replace bundled art or test on a clean checkout.

To add a series: extend `catalog.json`, add an asset under `app/src/main/assets/posters/`, and rebuild. No backend is required.
