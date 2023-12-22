from http.server import HTTPServer, BaseHTTPRequestHandler
import json
from util import *
from winterbridge import *
from threading import Thread
import logging

logging.basicConfig(filename='server.log', level=logging.INFO, format='%(asctime)s:%(levelname)s:%(message)s')

class HTTPHandler(BaseHTTPRequestHandler):

    def log_message(self, format, *args):
        # Override log_message to log to the file
        logging.info("%s - - [%s] %s\n" %
                (self.client_address[0],
                    self.log_date_time_string(),
                    format % args))

    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

        # Send message back to client
        message = "Hello, world! This is a response from the server."
        # Write content as utf-8 data
        self.wfile.write(bytes(message, "utf8"))


    def do_POST(self):
        content_length = int(self.headers['Content-Length'])
        # Read the data
        post_data = self.rfile.read(content_length)

        # Try to parse JSON data
        try:
            data = json.loads(post_data.decode())
            if True:
                with open("received_data.json", "w") as file:
                    json.dump(data, file, indent=4)
            response = "JSON data received and saved."
        except json.JSONDecodeError:
            response = "Failed to decode JSON data."

        # Send response
        self.send_response(200)
        self.end_headers()
        self.wfile.write(response.encode())

        # Handle to winterbridge
        thread = None
        if data['type']=='sort':
            thread = Thread(target=sort_items, args=(data['hotbar'],))
        if thread:
            thread.start()


httpd = HTTPServer(('', config.network.port), HTTPHandler)
httpd.serve_forever()
