from PIL import Image, ImageDraw, ImageFont

W, H = (400,400)
for i in range(31,128):
    msg = str(chr(i))
    font = ImageFont.truetype('arial.ttf', size=300)
    ascent, descent = font.getmetrics()
    (width, baseline), (offset_x, offset_y) = font.font.getsize(msg)
    # print(ascent, descent)
    # print(width, baseline)
    # print(offset_x, offset_y)

    im = Image.new("RGBA",(W,H),"white")

    draw = ImageDraw.Draw(im)
    w, h = draw.textsize(msg)
    # print(w,h)
    draw.text(((W - width)/2,(H - baseline - 2*offset_y)/2), msg, fill="black", font = font)

    im.save(str(i) + ".png", "PNG")