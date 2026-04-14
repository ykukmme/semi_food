from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import mm
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, HRFlowable
)
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
import os, re

# ── 폰트 등록 ──────────────────────────────────────────────────
font_candidates = [
    r"C:\Windows\Fonts\malgun.ttf",
    r"C:\Windows\Fonts\NanumGothic.ttf",
    r"C:\Windows\Fonts\gulim.ttc",
]
font_name = "Helvetica"
for path in font_candidates:
    if os.path.exists(path):
        try:
            pdfmetrics.registerFont(TTFont("KorFont", path))
            font_name = "KorFont"
            print(f"폰트 등록: {path}")
            break
        except Exception as e:
            print(f"폰트 로드 실패: {e}")

# ── 스타일 정의 ─────────────────────────────────────────────────
W, H = A4
margin = 20 * mm

def S(name, **kw):
    styles = getSampleStyleSheet()
    return ParagraphStyle(name, parent=styles["Normal"], fontName=font_name, **kw)

sTitle  = S("sTitle",  fontSize=20, spaceAfter=6,  spaceBefore=0,
            textColor=colors.HexColor("#1a1a2e"), leading=26)
sH1     = S("sH1",     fontSize=15, spaceAfter=4,  spaceBefore=14,
            textColor=colors.HexColor("#16213e"), leading=20)
sH3     = S("sH3",     fontSize=10, spaceAfter=2,  spaceBefore=8,
            textColor=colors.HexColor("#533483"), leading=14)
sBody   = S("sBody",   fontSize=9,  spaceAfter=3,  spaceBefore=1,  leading=14)
sBullet = S("sBullet", fontSize=9,  spaceAfter=2,  spaceBefore=1,  leading=13, leftIndent=12)
sQuote  = S("sQuote",  fontSize=8,  spaceAfter=4,  spaceBefore=4,  leading=12,
            leftIndent=12, textColor=colors.HexColor("#555555"),
            backColor=colors.HexColor("#f5f5f5"), borderPad=4)
# 코드 한 줄 — 한글 폰트 사용
sCodeLine = ParagraphStyle(
    "sCodeLine", fontName=font_name, fontSize=7.8,
    leading=12, spaceAfter=0, spaceBefore=0,
)

# ── 헬퍼 ────────────────────────────────────────────────────────
def escape(t):
    return t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

def inline(t):
    t = escape(t)
    t = re.sub(r"\*\*(.+?)\*\*", r"<b>\1</b>", t)
    t = re.sub(r"`(.+?)`",
               lambda m: '<font face="' + font_name + '" color="#c0392b">&#96;' + escape(m.group(1)) + '&#96;</font>',
               t)
    t = re.sub(r"\*(.+?)\*", r"<i>\1</i>", t)
    return t

def make_code_block(buf_lines):
    """코드 블록을 한글 폰트 Table로 렌더링"""
    avail = W - 2 * margin
    rows = []
    for line in buf_lines:
        n = len(line) - len(line.lstrip(" "))
        content = "&#160;" * n + escape(line.lstrip(" "))
        rows.append([Paragraph(content, sCodeLine)])
    if not rows:
        return None
    t = Table(rows, colWidths=[avail - 12])
    t.setStyle(TableStyle([
        ("BACKGROUND",    (0, 0),  (-1, -1), colors.HexColor("#f8f8f8")),
        ("BOX",           (0, 0),  (-1, -1), 0.5, colors.HexColor("#dddddd")),
        ("LEFTPADDING",   (0, 0),  (-1, -1), 8),
        ("RIGHTPADDING",  (0, 0),  (-1, -1), 8),
        ("TOPPADDING",    (0, 0),  (0,  0),  6),
        ("BOTTOMPADDING", (0, -1), (-1, -1), 6),
        ("TOPPADDING",    (0, 1),  (-1, -1), 1),
        ("BOTTOMPADDING", (0, 0),  (-1, -2), 1),
    ]))
    return t

def make_md_table(rows_raw):
    """마크다운 표 파싱 → reportlab Table"""
    rows = []
    for row in rows_raw:
        cells = [c.strip() for c in row.strip("|").split("|")]
        rows.append(cells)
    rows = [r for r in rows if not all(re.match(r"^[-: ]+$", c) for c in r)]
    if not rows:
        return None
    col_n = max(len(r) for r in rows)
    para_rows = []
    for ri, row in enumerate(rows):
        pcells = []
        for ci, cell in enumerate(row):
            style = ParagraphStyle(
                "tc_%d_%d" % (ri, ci),
                fontName=font_name, fontSize=8, leading=11,
                spaceAfter=0, spaceBefore=0,
                textColor=colors.white if ri == 0 else colors.black,
            )
            pcells.append(Paragraph(inline(cell), style))
        para_rows.append(pcells)
    avail = W - 2 * margin
    col_w = [avail / col_n] * col_n
    t = Table(para_rows, colWidths=col_w, repeatRows=1)
    t.setStyle(TableStyle([
        ("BACKGROUND",     (0, 0), (-1, 0),  colors.HexColor("#16213e")),
        ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, colors.HexColor("#f0f4ff")]),
        ("GRID",           (0, 0), (-1, -1), 0.3, colors.HexColor("#cccccc")),
        ("TOPPADDING",     (0, 0), (-1, -1), 5),
        ("BOTTOMPADDING",  (0, 0), (-1, -1), 5),
        ("LEFTPADDING",    (0, 0), (-1, -1), 7),
        ("RIGHTPADDING",   (0, 0), (-1, -1), 7),
        ("VALIGN",         (0, 0), (-1, -1), "MIDDLE"),
    ]))
    return t

# ── MD 파싱 ─────────────────────────────────────────────────────
with open(r"D:\study\semi\DB_DESIGN.md", encoding="utf-8") as f:
    md_lines = f.readlines()

flowables = []
in_code   = False
code_buf  = []
in_table  = False
table_buf = []

def flush_code():
    global code_buf
    if code_buf:
        blk = make_code_block(code_buf)
        if blk:
            flowables.append(Spacer(1, 4))
            flowables.append(blk)
            flowables.append(Spacer(1, 6))
        code_buf = []

def flush_table():
    global table_buf
    if table_buf:
        t = make_md_table(table_buf)
        if t:
            flowables.append(t)
            flowables.append(Spacer(1, 4))
        table_buf = []

i = 0
while i < len(md_lines):
    raw      = md_lines[i].rstrip("\n")
    stripped = raw.strip()

    # 코드 블록 토글
    if stripped.startswith("```"):
        if not in_code:
            in_code  = True
            code_buf = []
        else:
            in_code = False
            flush_code()
        i += 1
        continue
    if in_code:
        code_buf.append(raw)
        i += 1
        continue

    # 마크다운 표
    if stripped.startswith("|"):
        if not in_table:
            in_table  = True
            table_buf = []
        table_buf.append(stripped)
        i += 1
        continue
    else:
        if in_table:
            in_table = False
            flush_table()

    # 빈 줄
    if not stripped:
        flowables.append(Spacer(1, 4))
        i += 1
        continue

    # 인용문 >
    if stripped.startswith("> "):
        flowables.append(Paragraph(inline(stripped[2:]), sQuote))
        i += 1
        continue

    # 수평선
    if re.match(r"^-{3,}$", stripped):
        flowables.append(HRFlowable(width="100%", thickness=0.5,
                                    color=colors.HexColor("#cccccc"),
                                    spaceAfter=4, spaceBefore=4))
        i += 1
        continue

    # H1
    if stripped.startswith("# ") and not stripped.startswith("## "):
        flowables.append(Paragraph(inline(stripped[2:]), sTitle))
        flowables.append(HRFlowable(width="100%", thickness=1,
                                    color=colors.HexColor("#1a1a2e"),
                                    spaceAfter=6))
        i += 1
        continue

    # H3 / H4
    if stripped.startswith("### ") or stripped.startswith("#### "):
        text = re.sub(r"^#{3,4} ", "", stripped)
        flowables.append(Paragraph(inline(text), sH3))
        i += 1
        continue

    # H2
    if stripped.startswith("## "):
        flowables.append(Paragraph(inline(stripped[3:]), sH1))
        flowables.append(HRFlowable(width="100%", thickness=0.5,
                                    color=colors.HexColor("#16213e"),
                                    spaceAfter=3))
        i += 1
        continue

    # 불릿
    if stripped.startswith("- ") or stripped.startswith("* "):
        flowables.append(Paragraph("• " + inline(stripped[2:]), sBullet))
        i += 1
        continue

    # 일반 문단
    flowables.append(Paragraph(inline(stripped), sBody))
    i += 1

if in_code:  flush_code()
if in_table: flush_table()

# ── PDF 빌드 ────────────────────────────────────────────────────
out = r"D:\study\semi\DB_DESIGN.pdf"
doc = SimpleDocTemplate(
    out, pagesize=A4,
    leftMargin=margin, rightMargin=margin,
    topMargin=margin, bottomMargin=margin,
    title="스마트 식품 이커머스 — DB 설계 문서",
    author="semi project",
)
doc.build(flowables)
size = os.path.getsize(out)
print(f"PDF 생성 완료: {out}  ({size // 1024} KB)")
