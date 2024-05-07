from tkinter import *
from tkinter import ttk
from tkinter import filedialog as fd
from paths import PROJECT_ROOT

class Home(Frame):
    def __init__(self,  *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.file_paths = []
        self.file_callback = None
        self.configure(bg="white")
        self.columnconfigure(0, weight=1)
        self.columnconfigure(1, weight=1)
        
        self.welcome_label = ttk.Label(self, 
                                       text="Valitse tallennettavat tiedostot", 
                                       style="header.TLabel")
        self.welcome_label.grid(row=0, column=0, columnspan=2)
        # Button to  add files
        self.add_file_button = ttk.Button(self, text="Lisää tiedosto", 
                                          style="design.TButton" ,
                                          command=self.choose_files)
        self.add_file_button.grid(row=1, column=0, sticky='e')

        # Label to show chosen files
        self.add_file_label = ttk.Label(self, text="", style="files.TLabel")
        self.add_file_label.grid(row=1, column=1, sticky='w')

        # Button to send chosen files
        self.send_button = ttk.Button(self, text="Lähetä tiedostot", 
                                      style="design.TButton", 
                                      state="disabled")
        self.send_button.grid(row=4, column=0, sticky='e')

        # Label to show API messages
        self.message_label = ttk.Label(self, text="", style="message.TLabel")
        self.message_label.grid(row=5, column=0, columnspan=2)
        
        # Define visit
        self.visit_label = ttk.Label(self, 
                                     text="Valitse käynti, jos tieto ei ole datassa", 
                                     style="message.TLabel")
        self.visit_label.grid(row=2, column=0, columnspan=2)
        
        n = StringVar() 
        self.chosen_visit = ttk.Combobox(self, width = 27,  
                            textvariable = n) 
        self.chosen_visit['values'] = ('','1', '2', '3', '4', '5', '6', '7', 
                                       '8', '9', '10')
        self.chosen_visit.grid(row=3, column=0, columnspan=2, pady=20)

    def choose_files(self):
        # Only allow relevant filetypes
        filetypes = (("Excelit", "*.xlsx"), ("CSVt", "*.csv"), 
                     ("Tekstitiedostot", "*.txt"))
        f = fd.askopenfilenames(filetypes=filetypes, initialdir="./", 
                                multiple=True, 
                                title="Valitse lähetettävättiedostot") 
        self.add_file_label["text"] = f
        self.file_paths = f
        self.file_callback(f)
        self.update()
        
    
