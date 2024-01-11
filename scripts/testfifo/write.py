import datetime
import time

fp = open("pipe", "w")
for _ in range(3):
    print("Fuck1")
    fp.write(str(datetime.datetime.now()))
    print("Fuck2")
    fp.write('\n')
    print("Fuck3")
    fp.flush()
    print("Fuck4")
    time.sleep(1)
    print("Fuck5")

#fp.close()
