from http.server import HTTPServer, BaseHTTPRequestHandler
import logging
from winterbridge import current_thread, handle_skill, update_skill
from util import config
import shared
import threading
import time
from threading import Thread
import json

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
        t_s = time.time()
        content_length = int(self.headers['Content-Length'])
        # Read the data
        post_data = self.rfile.read(content_length)

        # Try to parse JSON data
        try:
            data = json.loads(post_data.decode())
            if type(data)==dict and 'type' in data and data['type']=='test':
                with open("received_data.json", "w") as file:
                    json.dump(data, file, indent=4)
            response = "OK"
        except json.JSONDecodeError:
            response = "Failed to decode JSON data."

        # Set response code
        self.send_response(200)
        self.end_headers()

        # Handle request
        global current_thread
        thread = None
        if data['type']=='info':
            with threading.Lock():
                if current_thread is not None and not current_thread.is_alive():
                    current_thread = None
                if current_thread is None:
                    response = "break"
                shared.need_update = True
                shared.latest_info = data

        elif data['type']=='cancel':
            with threading.Lock():
                current_thread = None
                shared.cancelled = True
                shared.cancel_cause = data['cause']

        else:
            # Handle to background
            if current_thread is None:
                thread = Thread(target=handle_skill, args=(data,))
                current_thread = thread
            else:
                thread = Thread(target=update_skill, args=(data,))


        # Set response body
        self.wfile.write(response.encode())

        if thread is not None:
            thread.start()

        t_t = time.time()
        if data['type']=='info':
            self.log_message("This %s post cost %fs", data['type'], t_t-t_s)


httpd = HTTPServer(('', config.network.port), HTTPHandler)
