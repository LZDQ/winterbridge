import requests
import json
import time

# URL of the server
url = 'http://localhost:1234/'

# Example data to be sent
data = {'name': 'John', 'age': 30}

# Convert dict to JSON bytestring
json_data = json.dumps(data).encode()

# Send POST request with JSON data
response = requests.post(url, data=json_data)

# Print server response
print(response.text)

while True:
    time.sleep(0.001)
    requests.post(url, data=json_data)
    print("Fuck")
