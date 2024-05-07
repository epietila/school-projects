from paths import PROJECT_ROOT
import api_connection
import csv, json


def handle_csv(paths, token):
    try:
        for path in paths:
            # Read the csv into a dictionary
            with open(path, "r") as f:
                reader = csv.DictReader(f)
                data_list = list(reader)
                data = json.dumps(data_list)

            # Try post the record to REDCap
            return api_connection.post_data(data, token)
          
    except Exception as e:
        return False, e