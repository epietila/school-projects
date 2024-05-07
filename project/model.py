from paths import TOKEN, KEY, KEE
from specials.crypts import decrypt_data

class Model:
    
    def __init__(self):
        self._hi = "hi"
    
    def get_token(self):
        try:
            with open(KEY, 'r') as f:
                token = decrypt_data(TOKEN, decrypt_data(KEE, f.readline()))
                return token
        except:
            return ""
        
    
    def write_log(self, log_text):
        with open("log.txt", "a") as f:
            f.write(log_text + "\n")