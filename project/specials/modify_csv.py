import csv

# Try to find visit number from the data
# Return: index of the visit column or -1 if not present
def check_visit(headers):
    i = 0
    for i in range(len(headers)):
        if headers[i].find("visit") != -1:
            if headers[i].find("date") == -1:
                return i
            else:
                return -1
        else:
            return -1
        

def create_new_csv(paths, sep, visitno):
    try:
        ofiles = []
        for path in paths:
            output_file = path + "recap.csv"
            ofiles.append(output_file)
            with open(path, 'r') as csv_input, open(output_file, 'w', newline='') as csv_output:    
                reader = csv.reader(csv_input)
                writer = csv.writer(csv_output)

                headers = ['record_id', 'redcap_event_name', 
                           'redcap_repeat_instrument', 'redcap_repeat_instance']
                first_row = True
                visit_index=-1

                # Write REDCap suitable csv
                for row in reader:
                    if first_row:
                        first_row = False
                        columns = [x.lower() for x in row[0].split(sep)]
                        writer.writerow(headers + columns)  
                        # Ensure correct visit number
                        if visit_index == -1: 
                            visit_index = check_visit(columns)
                    else:
                        record = row[0].split(sep)
                        # Extract values for new columns
                        record_id = record[0]
                        repeat_instrument = ""
                        repeat_instance = "1"

                        # Assign correct visit number
                        visit = ""
                        if visit_index > 0:
                            visit = "cumulative_visit_" + record[visit_index] + "_arm_1"
                        elif visitno != "":
                            visit = "cumulative_visit_" + visit + "_arm_1"
                        else:
                            return False, "Käyntiä ei löytynyt"
                        
                        # Combine all data for the row
                        new_row = [record_id, visit, repeat_instrument, repeat_instance] + record

                        writer.writerow(new_row)
                        print(new_row)

        return True, ofiles
            
    except Exception as e:
        return False, e