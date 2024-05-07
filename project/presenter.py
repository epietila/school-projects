import csv
from model import Model
from csv_to_json import handle_csv
from specials.modify_csv import create_new_csv

SUCCESS = "Tiedot lähetetty onnistuneesti."
REDCAPERROR = "Tiedon lähetys REDCapiin epäonnistui. Virhe: "
FILEERROR = "Tiedostojen käsittely epäonnistui"

class Presenter:

    def __init__(self, view):
        self.model = Model()
        self.view = view
        self.frame = self.view.frames["home"]
        self.frame.file_callback = self.handle_file_choosing
        self._bind() 
        self.view.start_mainloop()  
  
    def _bind(self):
        self.frame.send_button.config(command=self.create_record)

    def handle_file_choosing(self, files):
        if files:
            self.frame.send_button.config(state="active")
        else:
            self.frame.send_button.config(state="disabled")


    def create_record(self, sep="\t"):
        paths = self.frame.file_paths

        # Append the REDCap required fields
        new, paths = create_new_csv(paths, sep, self.frame.chosen_visit.get())
        print(new)
        if new:
            token = self.model.get_token()
            if token != "":
                # Try post the data to REDCap
                success, message = handle_csv(paths, token)
                if success:
                    self.show_message(SUCCESS)
                else:
                    self.show_message(REDCAPERROR + str(message))
            else: 
                self.show_message(FILEERROR)
        else:
            self.show_message(FILEERROR + str(paths))
        

    def show_message(self, message):
        self.frame.message_label["text"] = message
