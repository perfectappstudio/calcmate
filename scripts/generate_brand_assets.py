from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
import json

ROOT = Path(__file__).resolve().parents[1]
NAVY = '#14232D'
MINT = '#8CE4C5'
WHITE = '#F2F6F7'

def icon(size):
    scale = 4
    image = Image.new('RGB', (size*scale, size*scale), NAVY)
    draw = ImageDraw.Draw(image)
    unit = size*scale/108
    def rect(box, radius, fill):
        draw.rounded_rectangle(tuple(v*unit for v in box), radius=radius*unit, fill=fill)
    rect((31,24,77,84),8,WHITE)
    rect((35,28,73,80),5,NAVY)
    rect((40,33,68,46),3,MINT)
    rect((40,54,50,58),2,WHITE)
    rect((43,51,47,61),2,WHITE)
    rect((58,54,68,58),2,WHITE)
    rect((40,68,50,72),2,WHITE)
    rect((58,66,68,69),1.5,MINT)
    rect((58,72,68,75),1.5,MINT)
    return image.resize((size,size),Image.Resampling.LANCZOS)

assets=ROOT/'store-assets'
assets.mkdir(exist_ok=True)
icon(512).save(assets/'icon-512.png')
icon(1024).save(assets/'icon-1024.png')
for density,size in [('mdpi',48),('hdpi',72),('xhdpi',96),('xxhdpi',144),('xxxhdpi',192)]:
    icon(size).save(ROOT/f'app/src/main/res/mipmap-{density}/ic_launcher.webp', lossless=True)
ios=ROOT/'ios/CalcMate/Assets.xcassets/AppIcon.appiconset'
ios.mkdir(parents=True,exist_ok=True)
icon(1024).save(ios/'AppIcon.png')
(ios/'Contents.json').write_text(json.dumps({'images':[{'filename':'AppIcon.png','idiom':'universal','platform':'ios','size':'1024x1024'}],'info':{'author':'xcode','version':1}},indent=2))
(ios.parent/'Contents.json').write_text(json.dumps({'info':{'author':'xcode','version':1}}))
feature=Image.new('RGB',(1024,500),NAVY)
feature.paste(icon(380),(600,60))
draw=ImageDraw.Draw(feature)
font='/System/Library/Fonts/Supplemental/Avenir Next.ttc'
draw.text((64,130),'CalcMate',font=ImageFont.truetype(font,72),fill=WHITE)
draw.text((68,228),'Scientific calculator',font=ImageFont.truetype(font,28),fill=MINT)
draw.text((68,280),'Graphs · Equations · Conversions',font=ImageFont.truetype(font,23),fill=WHITE)
feature.save(assets/'feature-graphic-1024x500.png')
svg='''<svg xmlns="http://www.w3.org/2000/svg" width="512" height="512" viewBox="0 0 108 108"><rect width="108" height="108" fill="#14232D"/><rect x="31" y="24" width="46" height="60" rx="8" fill="#F2F6F7"/><rect x="35" y="28" width="38" height="52" rx="5" fill="#14232D"/><rect x="40" y="33" width="28" height="13" rx="3" fill="#8CE4C5"/><g fill="#F2F6F7"><rect x="40" y="54" width="10" height="4" rx="2"/><rect x="43" y="51" width="4" height="10" rx="2"/><rect x="58" y="54" width="10" height="4" rx="2"/><rect x="40" y="68" width="10" height="4" rx="2"/></g><g fill="#8CE4C5"><rect x="58" y="66" width="10" height="3" rx="1.5"/><rect x="58" y="72" width="10" height="3" rx="1.5"/></g></svg>'''
(assets/'icon.svg').write_text(svg)
