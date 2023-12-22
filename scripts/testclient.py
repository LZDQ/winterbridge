import requests
import json

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
