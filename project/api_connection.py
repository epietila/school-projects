import requests

data = {
    'token': '',
    'content': 'record',
    'action': 'import',
    'format': 'json',
    'type': 'flat',
    'overwriteBehavior': 'normal',
    'forceAutoNumber': 'false',
    'data': '',
    'returnContent': 'count',
    'returnFormat': 'json'
}

# Post a record to REDCap.
# Parameter: record as a variable in josn format
# Return: True if successful, if not, False and the error message
def post_data(json, api_token):
    try:
        data['token'] = api_token
        data['data'] = json
        r = requests.post('https://redcap.tuni.fi/redcap/api/',data=data)
        status = r.status_code

        if status == 200:
            return True, "success"
        else:
            return False, r.text
              
    except ConnectionError as e:
        return False, e
    except requests.exceptions.HTTPError as e:
        return False, e
    except requests.RequestException as e:
        return False, e
    except Exception as e:
        return False, e


