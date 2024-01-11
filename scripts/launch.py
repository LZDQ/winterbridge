import argparse
import shared
import util
import communicate
import status
import winterbridge
import os

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--stats', action=argparse.BooleanOptionalAction)
    parser.add_argument('--cpulim', action=argparse.BooleanOptionalAction)
    args = parser.parse_args()
    if args.stats:
        status.stats_parser.setup()
    if args.cpulim:
        os.system(f"cpulimit -p {os.getpid()} -b -c 5 -l 5")
    winterbridge.main()

