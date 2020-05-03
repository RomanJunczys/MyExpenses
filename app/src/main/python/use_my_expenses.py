import my_expenses


# How many days are left to be paid
# print("Days to payday: {}".format(my_expenses.get_days_to_payday(26)))

# What is my curent bank acount balance?

# need
my_expenses.set_file_name('csv/my_expenses.csv')

# the main solution
# print("Current my bank acount balance: {}".format(my_expenses.get_current_account_balance()))

# today is:
# print(my_expenses.get_str_today())

# last date in file
# print(my_expenses.get_last_date_in_file())

# expenses in the last week per day
# print("Weekly expenses per day: ")
# temp = my_expenses.get_weekly_expenses_per_day()
# print(temp)
# print(my_expenses.get_weekly_expenses_per_day())


# expenses in the last 7 days
print("Last 7 days expenses per day: ")
temp = my_expenses.get_expenses_from_last_seven_days()
print(temp)
# print(my_expenses.get_weekly_expenses_per_day())

