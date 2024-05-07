from presenter import Presenter
from views.view import View

def main():
    view = View()
    presenter = Presenter(view)

if __name__ == "__main__":
    main()