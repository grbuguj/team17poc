import easyocr
import sys
import re

reader = easyocr.Reader(['ko', 'en'])
results = reader.readtext(sys.argv[1])

# 날짜 정규표현식
date_patterns = [
    r'\b\d{4}\.\d{2}\.\d{2}\b',
    r'\b\d{2}\.\d{2}\.\d{2}\b',
    r'\b\d{2}\.\d{2}\b'
]

# 추출된 텍스트 중 날짜만 골라냄
for _, text, _ in results:
    for pattern in date_patterns:
        if re.match(pattern, text):
            print(text)
            sys.exit(0)

# 날짜 못 찾으면 전체 텍스트 출력
print("날짜 형식 없음:")
for _, text, _ in results:
    print(text)
