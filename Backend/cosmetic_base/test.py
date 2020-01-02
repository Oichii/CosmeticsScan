import requests
import json
rr = requests.get('http://127.0.0.1:8000/cosmetic')
print(rr.text)
content = {"id": 46}
c=json.dumps(content)
headers = {'content-type': 'application/json'}
r = requests.delete('http://127.0.0.1:8000/cosmetic/45')
print(r.text)
rr = requests.get('http://127.0.0.1:8000/cosmetic')
print(rr.text)