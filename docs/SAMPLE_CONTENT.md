# Sample browse content

Browse rows, posters, seasons, and related titles come from [`app/src/main/assets/catalog.json`](../app/src/main/assets/catalog.json). The optional top-level `metadata` block documents the sample catalog for humans and tools; the app reads `series` and image paths at runtime.

## Offline posters

Each entry may define `poster.assetPath` (for example `posters/sherlock.png`) under `app/src/main/assets/`. At runtime the app loads:

`file:///android_asset/<assetPath>`

Bundled PNGs ship with the repo so the browse UI works without network access. Each series has a **distinct** poster file (not a shared placeholder). To regenerate the bundled demo art after editing titles, run:

```bash
python3 scripts/generate_sample_posters.py
```

The generator produces simple, redistributable artwork keyed to each catalog title.

## Network fallback

When an asset file is missing, `MovieCatalog` uses the matching `fallbackUrl` from the catalog **if** *Network poster fallback* is enabled in **Settings** (search icon on the browse screen). Fallback URLs point at Google’s public Android TV sample bucket (`commondatastorage.googleapis.com/android-tv/Sample%20videos/...`), which is suitable for demos when you replace bundled art or test on a clean checkout.

To add a series: extend `catalog.json`, add an asset under `app/src/main/assets/posters/`, and rebuild. No backend is required.
