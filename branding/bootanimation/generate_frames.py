#!/usr/bin/env python3
"""
VorteXOS Boot Animation Frame Generator
Bu script, VorteXOS için PNG boot animasyon karelerini oluşturur.
Gereksinimler: pip install Pillow
"""

import os
import math
from PIL import Image, ImageDraw, ImageFont

WIDTH, HEIGHT = 1280, 720
BG_COLOR = (18, 18, 18)          # #121212
PURPLE    = (124, 77, 255)        # #7C4DFF
CYAN      = (0, 229, 255)         # #00E5FF
WHITE     = (255, 255, 255)

def draw_vortex_logo(draw, cx, cy, radius, alpha=255):
    """VorteX spiral logosu çizer."""
    for i in range(360):
        angle = math.radians(i)
        r = radius * (i / 360)
        x = cx + r * math.cos(angle)
        y = cy + r * math.sin(angle)
        size = max(2, int(4 * (i / 360)))
        a = int(alpha * (i / 360))
        color = (*PURPLE[:3], a)
        draw.ellipse([x-size, y-size, x+size, y+size], fill=color)

def draw_text_centered(draw, text, y, size=72, color=WHITE):
    """Metni yatayda ortala."""
    try:
        font = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", size)
    except Exception:
        font = ImageFont.load_default()
    bbox = draw.textbbox((0, 0), text, font=font)
    w = bbox[2] - bbox[0]
    draw.text(((WIDTH - w) // 2, y), text, fill=color, font=font)

def create_frame(frame_idx, total, folder, phase="intro"):
    """Tek bir animasyon karesi oluşturur."""
    img = Image.new("RGBA", (WIDTH, HEIGHT), BG_COLOR + (255,))
    draw = ImageDraw.Draw(img)

    cx, cy = WIDTH // 2, HEIGHT // 2
    progress = frame_idx / max(total - 1, 1)

    if phase == "intro":
        # Fade in: VorteX logosu büyüyerek açılır
        radius = int(180 * progress)
        alpha = int(255 * progress)
        draw_vortex_logo(draw, cx, cy - 80, radius, alpha)

        # "VorteXOS" yazısı fade in
        text_alpha = int(255 * max(0, (progress - 0.5) * 2))
        draw_text_centered(draw, "VorteXOS", cy + 120, size=80,
                           color=(255, 255, 255, text_alpha))
        draw_text_centered(draw, "Teknofest 2026", cy + 210, size=32,
                           color=(124, 77, 255, text_alpha))
    else:
        # Loop: dönen halka animasyonu
        angle_offset = progress * 360
        draw_vortex_logo(draw, cx, cy - 60, 150)
        # Dönen cyan halkası
        for i in range(8):
            a = math.radians(angle_offset + i * 45)
            x = cx + 200 * math.cos(a)
            y = (cy - 60) + 200 * math.sin(a)
            draw.ellipse([x-6, y-6, x+6, y+6], fill=CYAN)
        draw_text_centered(draw, "VorteXOS", cy + 110, size=64)

    fname = f"{frame_idx:04d}.png"
    img.save(os.path.join(folder, fname))

def main():
    base = os.path.dirname(os.path.abspath(__file__))
    part0 = os.path.join(base, "part0")
    part1 = os.path.join(base, "part1")
    os.makedirs(part0, exist_ok=True)
    os.makedirs(part1, exist_ok=True)

    print("Generating part0 (intro, 60 frames)...")
    for i in range(60):
        create_frame(i, 60, part0, phase="intro")
        if i % 10 == 0:
            print(f"  part0: {i}/60")

    print("Generating part1 (loop, 90 frames)...")
    for i in range(90):
        create_frame(i, 90, part1, phase="loop")
        if i % 10 == 0:
            print(f"  part1: {i}/90")

    print("All frames generated!")
    print("Pack with: zip -r0 bootanimation.zip desc.txt part0 part1")

if __name__ == "__main__":
    main()
