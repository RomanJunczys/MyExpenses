import pandas as pd
import matplotlib.pyplot as plt
from datetime import date
from datetime import datetime
from dateutil.relativedelta import relativedelta


# Read DataFrame account balance from .csv file
# my_file = '/storage/emulated/0/Android/data/pl.krakow.junczys.myexpenses/files/my_expenses.csv'
my_file = ''
# df_account_balance = pd.read_csv('csv/my_expenses.csv')

today = date.today()
current_account_balance = 0
int_payday = 26 # this is day of each month when I expect money
payday = date(today.year, today.month, int_payday)
days_to_payday = payday - today


def my_calculations():

    print("My file:", my_file)
    df_account_balance = pd.read_csv(my_file)

    df_account_balance['Date'] = pd.to_datetime(df_account_balance['Date'], dayfirst=True, errors='coerce')
    df_account_balance = df_account_balance.set_index('Date')
    df_account_balance.dropna()

    # Only in python's IDE
    print(df_account_balance)
    df_account_balance.plot()
    plt.show()

    # Current account balance - used in the report
    current_account_balance = float(df_account_balance.iloc[-1])
    print("Current account balance: {:.2f}".format(current_account_balance).replace(',',' '))

    # Expenses

    # Calculate expenses per month  ?? day, per week, per month.
    df_daily_diff = df_account_balance - df_account_balance.shift()
    df_daily_expenses = df_daily_diff.where(df_daily_diff < 0.0)
    df_daily_expenses.rename(columns={'Account balance': 'Expenses'}, inplace=True)
    df_daily_expenses.dropna(inplace=True)

    df_monthly_expences = df_daily_expenses.resample("M").sum()

    # Only in python's IDE
    # print(df_daily_expenses)
    # print(df_monthly_expences)
    df_monthly_expences.plot()
    plt.show()


    expenses_in_the_current_month = float(df_monthly_expences.iloc[-1]) # used in the report


    # Earnings

    df_daily_earnings = df_daily_diff.where(df_daily_diff > 0.0)
    df_daily_earnings.rename(columns={'Account balance': 'Earnings'}, inplace=True)
    df_daily_earnings.dropna(inplace=True)


    df_monthly_earnings = df_daily_earnings.resample("M").sum()

    # Only in python's IDE
    # print(df_daily_earnings)
    # print(df_monthly_earnings)
    df_monthly_earnings.plot()
    plt.show()

    earnings_in_the_last_month = float(df_monthly_earnings.iloc[-1]) # Earnings in the last month - used in the report
    print("Earnings in the last mont: {:,.2f}".format(earnings_in_the_last_month).replace(',',' '))

    # Days

    today = date.today()
    print("Today is: {}".format(today))

    # count the days to pay
    int_payday = 26 # this is day of each month when I expect money
    payday = date(today.year, today.month, int_payday)
    days_to_payday = payday - today
    if days_to_payday.days < 0:
        payday = datetime(today.year, today.month, int_payday) + relativedelta(months=1)
        days_to_payday = payday - today

    print("Pay day: {}".format(payday))
    print("Days to the pay day: {:d}".format(days_to_payday.days))

    html_text = ''

    html_text += "<H1> Today: " + today.strftime('%d-%m-%Y') + "</H1>" + "\n"
    html_text += "<H2> Account balance: " + "{:,.2f}".format(current_account_balance).replace(',', ' ') + "</H2>" + "\n"
    html_text += "<H2> Days left: " + str(days_to_payday.days) + "</H2>" + "\n"
    html_text += "<H2> Per day: "  + "{:,.2f}".format(current_account_balance / days_to_payday.days).replace(',',' ') + "</H2>" + "\n"

    return html_text


# Reports


def very_simple_report():
    html_text = ''

    html_text += "<H1>" + today.strftime('%d-%m-%Y') + "</H1>"
    html_text += "<H2>" + "{:,.2f}".format(current_account_balance).replace(',', ' ') + "</H2>"
    html_text += "<H2>" + str(days_to_payday.days) + "</H2>"
    html_text += "<H2>" + "{:,.2f}".format(current_account_balance / days_to_payday.days).replace(',',' ') + "</H2>"

    return html_text


def simple_report():

    html_text = ''

    html_text += "<H1> Today: " + today.strftime('%d-%m-%Y') + "</H1>" + "\n"
    html_text += "<H2> Account balance: " + "{:,.2f}".format(current_account_balance).replace(',', ' ') + "</H2>" + "\n"
    html_text += "<H2> Days left: " + str(days_to_payday.days) + "</H2>" + "\n"
    html_text += "<H2> Per day: "  + "{:,.2f}".format(current_account_balance / days_to_payday.days).replace(',',' ') + "</H2>" + "\n"

    return html_text


def report():
    
    html_text = ''
    
    html_text += "<H1>Report of "+today.strftime('%d-%m-%Y')+"</H1>"
    html_text += "<H2>The account balance is "+"{:,.2f}".format(current_account_balance).replace(',',' ')+"</H2>"
    html_text += "<H2>There are " + str(days_to_payday.days)+ " days left until the payday"+"</H2>"
    html_text += "<H2>You can spend " + "{:,.2f}".format(current_account_balance/days_to_payday.days).replace(',',' ') +" a day"+"</H2>"
    html_text += "<H2>Earnings in the last month: " + '{:,.2f}'.format(earnings_in_the_last_month).replace(',',' ') + " </H2>"
    html_text += "<H2>Expenses in the current month: " + '{:,.2f}'.format(expenses_in_the_current_month).replace(',',' ') + " </H2>"
    
    return html_text




# Tests

print(simple_report())
