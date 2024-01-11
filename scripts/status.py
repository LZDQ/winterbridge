from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import undetected_chromedriver as uc
import time
import os
import shared


class PlayerStatus:
    def __init__(self, name, driver):
        t1 = time.time()
        self.name = name
        try:
            driver.get("https://blocksmc.com/player/" + name)
            x = driver.find_elements(By.ID, "BedWars:Teams")
            assert x
        except:
            print("Error parsing")
            self.ok = False
            return
        x = x[0]
        for l in x.children():
            #breakpoint()
            d = l.children()
            setattr(self, d[0].text, int(d[1].text))
        self.ok = True
        # Calculate
        # Keys: Points Wins Kills Deaths Beds Played
        self.kd = self.Kills / max(self.Deaths, 1)
        self.wlr = self.Wins / max(self.Played - self.Wins, 1)
        self.br = self.Beds / max(self.Played, 1)
        t2 = time.time()
        print("Parsed:", name)
        print("Time used:", t2-t1)


class StatusParser:

    def __init__(self):
        self.built = False

    def setup(self):
        chrome_options = Options()
        #chrome_options.add_argument("--headless")
        chrome_options.add_argument("--user-data-dir=/home/ldq/.config/google-chrome")
        chrome_options.page_load_strategy = 'eager'
        #self.driver = webdriver.Chrome(options=chrome_options)
        self.driver = uc.Chrome(options=chrome_options)

        self.driver.get("https://blocksmc.com/player/lzdq")
        x = self.driver.find_elements(By.ID, "BedWars:Teams")
        if not x:
            print("Verify human")
        else:
            print("Successfully setup selenium")
            self.built = True
        self.parsed_players = dict()
        self.player_list = []  # Not used

    def update_stats(self):
        if not self.built:
            return
        while True:
            players = shared.players_que.get()
            print("Updating stats")
            self.player_list = players
            while shared.players_que.empty():
                self.show_stats()
                ok = False
                for name in players:
                    if name not in self.parsed_players:
                        stat = PlayerStatus(name, driver=self.driver)
                        if stat.ok:
                            self.parsed_players[name] = stat
                            ok = True
                            break
                if not ok:
                    break
            self.show_stats()

    def show_stats(self):
        stats = []
        for name in self.player_list:
            if name in self.parsed_players:
                stats.append(self.parsed_players[name])
        stats.sort(key=lambda s: s.wlr, reverse=True)

        os.system('clear')
        vals = ['wlr', 'br', 'kd', 'Beds', 'Kills', 'Played']
        row_head = "%20s" % "Name"
        for val in vals:
            row_head += " | "
            row_head += "%8s" % val
        print(row_head)
        print('-------------------------------------------------------------------------------------------')
        for s in stats:
            name = s.name
            row = "%20s" % (name if len(name)<=20 else name[:20])
            for val in vals:
                row += " | "
                x = getattr(s, val, None)
                if type(x)==int:
                    row += "%8d" % x
                elif type(x)==float:
                    row += "%8.2f" % x
                else:
                    row += "%8s" % "None"
            print(row)


stats_parser = StatusParser()

