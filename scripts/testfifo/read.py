import time

#fp = open("/home/ldq/mc/winterbridge/scripts/pipe", "r")
fp = open("pipe", "r")
while True:
    print(fp.readline())
    time.sleep(5)

