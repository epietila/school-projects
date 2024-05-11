from Cryptodome.Cipher import AES
from Cryptodome.Util.Padding import unpad
from base64 import  b64decode

def decrypt(text, key):
    key = b64decode(key)
    ciphertext = b64decode(text)

    # Extract IV and ciphertext from the decoded data
    iv = ciphertext[:16]
    ciphertext = ciphertext[16:]
    cipher = AES.new(key, AES.MODE_CBC, iv)

    # Decrypt and unpad the ciphertext
    decrypted_data = cipher.decrypt(ciphertext)
    unpadded_data = unpad(decrypted_data, AES.block_size)

    # Return the decrypted data as a UTF-8 encoded string
    return unpadded_data.decode('utf-8')

def decrypt_data(file_path, key):
    try:
        with open(file_path, 'rb') as file:
            encrypted_data = file.read()
        
        return decrypt(encrypted_data, key)

    except FileNotFoundError:
        print('File not found.')
        return ""
    
    except Exception as e:
        print('Invalid key.')
        return ""
