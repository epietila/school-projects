from cryptography.fernet import Fernet, InvalidToken

def decrypt_data(file_path, key):
    f = Fernet(key)
    try:
        with open(file_path, 'rb') as file:
            encrypted_data = file.read()

        return f.decrypt(encrypted_data).decode('utf-8')

    except FileNotFoundError:
        print('File not found.')
        return ""
    
    except InvalidToken:
        print('Invalid key.')
        return ""
    
