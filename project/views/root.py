from tkinter import Tk
from tkinter import ttk

class Root(Tk):
    def __init__(self):
        super().__init__()

        start_width = 500
        min_width = 400
        start_height = 500
        min_height = 450
        self.configure(bg="white")

        self.geometry(f"{start_width}x{start_height}")
        self.minsize(width=min_width, height=min_height)
        self.title("REDCap connector")
        self.grid_columnconfigure(0, weight=1)
        self.grid_columnconfigure(1, weight=1)
        self.grid_rowconfigure(0, weight=1)
        self.grid_rowconfigure(1, weight=1)
        self.grid_rowconfigure(2, weight=1)

        style=ttk.Style()
        green = "#1B7B34"
        coral = "#F24C4E"
        turquoise = "#1FB58F"
        yellow = "#EAB126"
        style.configure("design.TButton",
                foreground='white',
                background=green,
                font=("Arial", 11),
                padding=10)
        style.configure("header.TLabel",foreground=coral, background='white',
                         font="Arial 16", padding=10)
        style.configure("message.TLabel",foreground=coral, background='white',
                         font="Arial 12", padding=10)
        style.configure("files.TLabel",foreground='black', background='white',
                         font="Arial 11", padding=10)

