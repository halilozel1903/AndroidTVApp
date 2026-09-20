#!/usr/bin/env python3
"""Generate distinct bundled poster/background PNGs for the sample TV catalog."""

from __future__ import annotations

import math
import os
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "app" / "src" / "main" / "assets"

POSTER_SIZE = (320, 480)
BACKGROUND_SIZE = (1920, 1080)


def _lerp(a: float, b: float, t: float) -> float:
    return a + (b - a) * t


def _gradient(size: tuple[int, int], top: tuple[int, int, int], bottom: tuple[int, int, int]) -> Image.Image:
    width, height = size
    img = Image.new("RGB", size)
    pixels = img.load()
    for y in range(height):
        t = y / max(height - 1, 1)
        color = tuple(int(_lerp(top[i], bottom[i], t)) for i in range(3))
        for x in range(width):
            pixels[x, y] = color
    return img


def _load_font(size: int) -> ImageFont.FreeTypeFont | ImageFont.ImageFont:
    for path in (
        "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
        "/usr/share/fonts/truetype/liberation/LiberationSans-Bold.ttf",
    ):
        if os.path.exists(path):
            return ImageFont.truetype(path, size=size)
    return ImageFont.load_default()


def _draw_poster(path: Path, title: str, subtitle: str, top: tuple[int, int, int], bottom: tuple[int, int, int]) -> None:
    img = _gradient(POSTER_SIZE, top, bottom)
    draw = ImageDraw.Draw(img)
    accent = tuple(min(255, c + 40) for c in bottom)
    draw.rectangle((0, POSTER_SIZE[1] - 8, POSTER_SIZE[0], POSTER_SIZE[1]), fill=accent)

    title_font = _load_font(34)
    sub_font = _load_font(18)
    margin = 24
    draw.multiline_text(
        (margin, POSTER_SIZE[1] - 160),
        title,
        fill=(255, 255, 255),
        font=title_font,
        spacing=4,
    )
    draw.text((margin, POSTER_SIZE[1] - 56), subtitle, fill=(220, 220, 220), font=sub_font)

    # Subtle diagonal highlight
    overlay = Image.new("RGBA", POSTER_SIZE, (255, 255, 255, 0))
    o_draw = ImageDraw.Draw(overlay)
    for i in range(0, POSTER_SIZE[0] + POSTER_SIZE[1], 18):
        o_draw.line((i, 0, i - POSTER_SIZE[1], POSTER_SIZE[1]), fill=(255, 255, 255, 12), width=6)
    img = Image.alpha_composite(img.convert("RGBA"), overlay).convert("RGB")

    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path, format="PNG", optimize=True)


def _draw_background(path: Path) -> None:
    img = _gradient(BACKGROUND_SIZE, (18, 32, 58), (6, 10, 18))
    draw = ImageDraw.Draw(img)
    for x in range(0, BACKGROUND_SIZE[0], 120):
        alpha = 30 + int(20 * math.sin(x / 180))
        draw.line((x, 0, x - 400, BACKGROUND_SIZE[1]), fill=(alpha, alpha + 20, alpha + 40), width=3)
    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path, format="PNG", optimize=True)


def main() -> None:
    posters = [
        ("posters/sherlock.png", "Sherlock", "BBC One", (12, 45, 92), (3, 12, 28)),
        ("posters/stranger_things.png", "Stranger\nThings", "Netflix", (120, 18, 38), (20, 4, 12)),
        ("posters/black_mirror.png", "Black\nMirror", "Netflix", (28, 28, 32), (8, 8, 10)),
        ("posters/dark.png", "Dark", "Netflix", (34, 52, 28), (12, 18, 10)),
        ("posters/new_amsterdam.png", "New\nAmsterdam", "NBC", (10, 88, 110), (4, 32, 48)),
    ]
    for rel, title, subtitle, top, bottom in posters:
        _draw_poster(ASSETS / rel, title, subtitle, top, bottom)

    _draw_background(ASSETS / "backgrounds/grid_selection.png")

    orphan = ASSETS / "posters/grid_selection.png"
    if orphan.exists():
        orphan.unlink()


if __name__ == "__main__":
    main()
