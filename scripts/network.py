from http.server import HTTPServer, BaseHTTPRequestHandler
import json
from util import config, cancel_t, get_window_info
from winterbridge import sort_items, block_in, test
from threading import Thread
import logging

logging.basicConfig(filename='server.log', level=logging.INFO, format='%(asctime)s:%(levelname)s:%(message)s')

sensitivity_log = []

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

        if data['type']=='test':
            #print('Direction:', data['dir'])
            #print('Position:', data['pos'])
            #print('Eye:', data['eye'])
            #print('Block:', data['blocks'])
            thread = Thread(target=test, args=(
                #(data['pos']['x'], data['pos']['y'], data['pos']['z']),
                #(data['eye']['x'], data['eye']['y'], data['eye']['z']),
                #(data['dir']['x'], data['dir']['y']),
                list(data['pos'].values()),
                list(data['eye'].values()),
                list(data['dir'].values()),
                data['blocks'],
                ))
            sensitivity_log.append(data['dir'])
            if False and len(sensitivity_log)==2:
                window = get_window_info()
                # data['pos']['x'] is the pitch. Looking up: -90, looking down: 90
                print('Sensitivity pitch:', (sensitivity_log[1]['x']-sensitivity_log[0]['x'])/(window.HEIGHT//4))
                print('Sensitivity yaw:', (sensitivity_log[1]['y']-sensitivity_log[0]['y'])/(window.WIDTH//4))


        if data['type']=='blockin':
            thread = Thread(target=block_in, args=(
                list(data['pos'].values()),
                list(data['eye'].values()),
                list(data['dir'].values()),
                data['blocks'],
                ))

        if data['type']=='cancel':
            cancel_t = time.time()

        if thread:
            thread.start()


httpd = HTTPServer(('', config.network.port), HTTPHandler)
httpd.serve_forever()
