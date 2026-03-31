from flask import Flask, render_template, request, redirect, url_for, session, flash,get_flashed_messages, send_from_directory, abort,jsonify
from flask_mysqldb import MySQL
import MySQLdb.cursors
from datetime import datetime
import os
import sys
# from helper import Helper

app = Flask(__name__)

app.secret_key = "abcd21234455"
app.config["MYSQL_HOST"] = "localhost"
app.config["MYSQL_USER"] = "root" #"root"
app.config["MYSQL_PASSWORD"] = "mysql" #"mysql"
app.config["MYSQL_DB"] = "cs6400_phase2"
app.config["MYSQL_UNIX_SOCKET"] = "/Applications/AMPPS/apps/mysql/var/mysql.sock"

mysql = MySQL(app)


@app.route('/<string:page>.html')
def serve_login():
    try:
        directory = Helper.getTemplatesDir()
        return send_from_directory(directory, '{escape(page)}.html')
    except FileNotFoundError:
        abort(404)

@app.route('/')
def index():
    return redirect(url_for('login'))

@app.route("/login")
def login():
    return render_template('login.html')

@app.route("/logout")
def logout():
    session.pop('logged_in', None)
    session.pop('usesr_id', None)
    return redirect(url_for('login'))



@app.route("/home/<string:user_id>")
def home(user_id):
    if not "logged_in"  in session : return redirect(url_for('login'))
    cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
    cursor.execute("SELECT first_name, last_name FROM `User` WHERE employeeID = %s", (user_id,))
    result = cursor.fetchone()
    
    first_name=result["first_name"]
    last_name=result["last_name"]

    cursor.execute("""
        SELECT 
        (SELECT COUNT(DISTINCT Store.store_number) FROM Store) AS store_count,
        (SELECT COUNT(DISTINCT City.city_name) FROM City) AS city_count,
        (SELECT COUNT(DISTINCT District.district_number) FROM District) AS district_count,
        (SELECT COUNT(DISTINCT Manufacture.manufacture_name) FROM Manufacture) AS manufacture_name,       
        (SELECT COUNT(DISTINCT Product.PID) FROM Product) AS product_count,      
        (SELECT COUNT(DISTINCT Category.category_name) FROM Category) AS category_count,
        (SELECT COUNT(DISTINCT Holiday.holiday_name) FROM Holiday) AS holiday_name_count,
        (SELECT COUNT(DISTINCT Holiday.business_date) FROM Holiday) AS holiday_date_count;
    """)
    counts = cursor.fetchone()
    print(counts)

    # Close the database connection
    cursor.close()
    
    # Render the home template with the welcome message and counts
    return render_template('home.html', user_id=user_id,first_name=first_name, last_name=last_name, counts=counts)


def user_has_all_district(employeeID):
    try:
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)

        # Get the user's assigned district
        cursor.execute('SELECT districts_assigned FROM User WHERE employeeID  = %s', (employeeID,))
        user = cursor.fetchone()
        districts_assigned = user['districts_assigned'].split(',')

        # Get all district from the district table
        cursor.execute('SELECT COUNT(district_number) as district_count FROM District')
        district_count_result = cursor.fetchone()
        district_count = district_count_result['district_count']

        cursor.close()

        print(f"User's assigned districts: {districts_assigned}")
        print(f"Total number of districts: {district_count}")

        return len(districts_assigned) == district_count
    except Exception as e:
        print(f"Error in user_has_all_districts: {e}")
        return False

def get_assigned_district(employeeID):
    cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
    cursor.execute('SELECT districts_assigned FROM User WHERE employeeID  = %s', (employeeID,))
    user = cursor.fetchone()
    cursor.close()
    if user and user['districts_assigned']:
        return user['districts_assigned'].split(',')
    return []


@app.route("/manufacture", methods=['GET'])
def manufacture():
    if not "logged_in" in session:
        return redirect(url_for('login'))

    cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
    cursor.execute("""
        SELECT manufacture_name, COUNT(*) AS count, ROUND(AVG(retail_price), 2) AS avg_price,
            MIN(retail_price) AS min_price, MAX(retail_price) AS max_price
        FROM Product
        GROUP BY manufacture_name
        ORDER BY avg_price DESC
        LIMIT 100
    """)
    manufactures = cursor.fetchall()
    cursor.close()

    view_report_insert_log("Manufacturer's Product Report")
    return render_template('manufacture.html', manufactures=manufactures)


@app.route("/manufacture/<manufacture_name>")
def view_specific_manufacture(manufacture_name):
    if not "logged_in" in session:
        return redirect(url_for('login'))

    cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
    
    # Query to get manufacture details: name, total products, average price, min price, max price
    cursor.execute("""
        SELECT manufacture_name, COUNT(*) AS count, ROUND(AVG(retail_price), 2) AS avg_price,
            MIN(retail_price) AS min_price, MAX(retail_price) AS max_price
        FROM Product
        WHERE manufacture_name = %s
        GROUP BY manufacture_name
    """, (manufacture_name,))
    manufacture_details = cursor.fetchone()
    
    # Query to get products for the specific manufacture
    cursor.execute("""
        SELECT Product.PID, pname, retail_price, GROUP_CONCAT(category_name SEPARATOR ', ') AS categories
        FROM Product
        JOIN ProductCategory ON Product.PID = ProductCategory.PID
        WHERE Product.manufacture_name = %s
        GROUP BY Product.PID, pname, retail_price
        ORDER BY retail_price DESC
    """, (manufacture_name,))
    products = cursor.fetchall()
    cursor.close()

    return render_template('manufacture_details.html', manufacture=manufacture_details, products=products)



@app.route("/category", methods=["GET", "POST"])
def category():
    if not "logged_in"  in session : return redirect(url_for('login'))
    cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
    query = """
    SELECT 
        c.category_name AS CategoryName, 
        (SELECT COUNT(*) 
         FROM Product p 
         JOIN ProductCategory pc ON p.PID = pc.PID 
         WHERE pc.category_name = c.category_name) AS Product_Number, 
        (SELECT COUNT(DISTINCT p.manufacture_name) 
         FROM Product p 
         JOIN ProductCategory pc ON p.PID = pc.PID 
         WHERE pc.category_name = c.category_name) AS Manufacturer_Number, 
        (SELECT ROUND(AVG(p.retail_price), 2) 
         FROM Product p 
         JOIN ProductCategory pc ON p.PID = pc.PID 
         WHERE pc.category_name = c.category_name) AS AverageRetailPrice 
    FROM Category c 
    ORDER BY c.category_name ASC;
    """
    cursor.execute(query)
    data = cursor.fetchall()
    cursor.close()
    
    view_report_insert_log("Category Report")
    return render_template('category.html',data=data)

@app.route("/actual_predicted_revenue",methods=['GET', 'POST'])
def actual_predicted_revenue():
    if not "logged_in"  in session : return redirect(url_for('login'))

    assigned_districts = session['districts_assigned']
    district_filter = ','.join(["'%s'" % district for district in assigned_districts])
    cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)

    query = f"""
    SELECT
        p.PID AS ProductID,
        p.pname AS ProductName,
        p.retail_price AS RetailPrice,
        SUM(s.quantity) AS TotalQuantitySold,
        SUM(CASE WHEN d.discount_price IS NOT NULL THEN s.quantity ELSE 0 END) AS TotalDiscountedQuantitySold,
        SUM(CASE WHEN d.discount_price IS NULL THEN s.quantity ELSE 0 END) AS TotalRetailQuantitySold,
        ROUND(SUM(
            CASE
                WHEN d.discount_price IS NOT NULL THEN (d.discount_price * s.quantity)  
                ELSE p.retail_price * s.quantity
            END
        ),2) AS ActualRevenue,
        ROUND(SUM(
            CASE
                WHEN d.discount_price IS NOT NULL THEN s.quantity * 0.75 * p.retail_price
                ELSE s.quantity * p.retail_price
            END
        ),2) AS ExpectedRevenue,
        ROUND(SUM(
            CASE
                WHEN d.discount_price IS NOT NULL THEN (d.discount_price * s.quantity) 
                ELSE p.retail_price * s.quantity
            END
        ) - SUM(
            CASE
                WHEN d.discount_price IS NOT NULL THEN s.quantity * 0.75 * p.retail_price
                ELSE s.quantity * p.retail_price
            END
        ),2) AS ActualExpectedDifference
    FROM Sold s
    JOIN Product p ON s.PID = p.PID
    JOIN ProductCategory pc ON pc.PID = p.PID
    LEFT JOIN Discount d ON d.PID = p.PID AND d.business_date = s.business_date
    JOIN Store st ON st.store_number = s.store_number
    WHERE pc.category_name = 'GPS'
      AND st.district_number IN ({district_filter})
    GROUP BY p.PID, p.pname, p.retail_price
    HAVING ABS(ActualExpectedDifference) > 200
    ORDER BY ActualExpectedDifference DESC;
    """

    cursor.execute(query)
    actual_predicted_revenue = cursor.fetchall()
    cursor.close()
    # Debugging output
    # for result in actual_predicted_revenue:
    #     print(result)

    view_report_insert_log("Actual versus Predicted Revenue for GPS units")
    return render_template('actual_predicted_revenue.html',data=actual_predicted_revenue)

@app.route("/groundhog", methods=["GET", "POST"])
def groundhog():
    if not "logged_in"  in session : return redirect(url_for('login'))

    districts_assigned = session.get('districts_assigned', [])
    if not districts_assigned:
        flash('No district assigned to the user.', 'danger')
        return redirect(url_for('index'))
    district_filter = ','.join(["'%s'" % district for district in districts_assigned])
    try:
        cursor = mysql.connection.cursor()
        query = f"""
        SELECT
            YEAR(s.business_date) AS Year,
            SUM(s.quantity) AS TotalQuantity,
            (SUM(s.quantity) / 365.0) AS AveragePerDay,
            SUM(CASE
                WHEN MONTH(s.business_date) = 2 AND DAY(s.business_date) = 2 THEN s.quantity
                ELSE 0
            END) AS GroundhogDayQuantity
        FROM Sold s
        JOIN Product p ON s.PID = p.PID
        JOIN ProductCategory pc ON p.PID = pc.PID
        JOIN Store st ON s.store_number = st.store_number
        WHERE pc.category_name = 'Air Conditioning'
          AND st.district_number IN ({district_filter})
        GROUP BY YEAR(s.business_date)
        ORDER BY YEAR(s.business_date) ASC;
        """
        cursor.execute(query)
        groundhog_report = cursor.fetchall()
        cursor.close()

        flash({"data":groundhog_report},"success")

        view_report_insert_log("Air Conditioners on Groundhog Day?")

        return render_template('groundhog.html')
    except Exception as e:
        print(e)
        flash('An error occurred while fetching groundhog data.', 'danger')
        return redirect(url_for('index'))

@app.route("/revenue_state", methods=['GET', 'POST'])
def revenue_state():
    if not "logged_in" in session:
        return redirect(url_for('login'))
    
    if not session.get('has_all_district', False):
        print(session.get('has_all_district'))
        return redirect('invalid.html') 

    # Get unique states
    cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
    cursor.execute("SELECT DISTINCT state FROM City")
    states = cursor.fetchall()
    cursor.close()

    selected_state = None
    results = []

    if request.method == 'POST':
        selected_state = request.form.get('state')
        print(f"Received POST request with state: {selected_state}")  # Debug 

        # Query to fetch store revenue by year by state
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute("""
            SELECT Store.store_number, City.city_name,
            EXTRACT(YEAR FROM Sold.business_date) AS year,
            ROUND(SUM(
                CASE WHEN Discount.business_date = Sold.business_date THEN Sold.quantity * Discount.discount_price
                ELSE Sold.quantity * Product.retail_price
            END ), 2) AS total_revenue
            FROM Store
            JOIN Sold ON Store.store_number = Sold.store_number
            JOIN Product ON Product.PID = Sold.PID
            JOIN City ON City.state = Store.state AND City.city_name = Store.city_name
            LEFT JOIN Discount ON Discount.business_date = Sold.business_date
            WHERE City.state = %s
            GROUP BY Store.store_number, City.city_name, year
            ORDER BY year ASC, total_revenue DESC
        """, (selected_state,))
        results = cursor.fetchall()
        cursor.close()

        # print(f"Query results: {results}")  # Debug

        view_report_insert_log("Store Revenue by Year by State")
    return render_template('revenue_state.html', selected_state=selected_state, states=states, results=results)

@app.route('/select_date', methods=['GET', 'POST'])
def select_date():
    if 'logged_in' not in session:
        return redirect(url_for('login'))
    if request.method == 'POST':
        start_date = request.form['start_date']
        end_date = request.form['end_date']
        return redirect(url_for('main_report', start_date=start_date, end_date=end_date))
    return render_template('select_date.html', has_all_district=session.get('has_all_district', False))

@app.route("/district_volume", methods=['GET', 'POST'])
def district_volume():
    if not "logged_in"  in session : return redirect(url_for('login'))
    if not session.get('has_all_district', False):
        print(session.get('has_all_district'))
        return redirect('invalid.html') 
    
    years_list =[]
    months_list=[]

    try:
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)

        # Get available years and months from the database
        cursor.execute("SELECT DISTINCT YEAR(business_date) as year FROM Sold")
        years_list = [row['year'] for row in cursor.fetchall()]
        cursor.execute("SELECT DISTINCT MONTH(business_date) as month FROM Sold")
        months_list = [row['month'] for row in cursor.fetchall()]


        if request.method == 'POST':
            selected_year = request.form['year']
            selected_month = request.form['month']

            print(selected_year,selected_month)
            if selected_year and selected_month:
                start_date = f"{selected_year}-{selected_month}-01"
                end_date = f"{selected_year}-{selected_month}-31"

                query = """
                WITH DistrictCategory AS (
                    SELECT 
                        ProductCategory.category_name, 
                        District.district_number, 
                        SUM(Sold.quantity) AS total_quantity
                    FROM Sold 
                    JOIN Product ON Sold.PID = Product.PID
                    JOIN Store ON Sold.store_number = Store.store_number
                    JOIN District ON Store.district_number = District.district_number
                    JOIN ProductCategory ON Product.PID = ProductCategory.PID
                    WHERE Sold.business_date BETWEEN %s AND %s
                    GROUP BY District.district_number, ProductCategory.category_name
                ),
                RankedDistricts AS (
                    SELECT 
                        category_name, 
                        district_number, 
                        total_quantity,
                        ROW_NUMBER() OVER (PARTITION BY category_name ORDER BY total_quantity DESC) AS rn
                    FROM DistrictCategory
                )
                SELECT 
                    category_name, 
                    district_number, 
                    total_quantity AS max_unit
                FROM RankedDistricts
                WHERE rn = 1
                ORDER BY category_name ASC;
                """
                cursor.execute(query, (start_date, end_date))
                data = cursor.fetchall()

                cursor.close()

                # print(data)
                view_report_insert_log("District with Highest Volume for each Category")
                return render_template('district_volume.html',
                           data=data,
                           years_list=years_list,
                           months_list=months_list,
                           selected_year=selected_year,
                           selected_month=selected_month,
                           start_date=start_date,
                           end_date=end_date)
            

    except Exception as e:
        print(f"Error in district_volume: {e}")
        flash('An error occurred while generating the report.', 'danger')



    return render_template('district_volume.html',
                    years_list=years_list,
                    months_list=months_list,
                    selected_year=None,
                    selected_month=None,
                    )
    


@app.route('/district_volume_details/<category>/<district>/<start_date>/<end_date>')
def district_volume_details(category, district,start_date,end_date):
    if 'logged_in' not in session or not session.get('has_all_district', False):
        abort(403)
        
    if not session.get('has_all_district', False):
        print(session.get('has_all_district'))
        return redirect('invalid.html') 
    
    # start_date = request.args.get('start_date')
    # end_date = request.args.get('end_date')

    try:
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)

        query = """
        SELECT 
            Store.district_number, 
            Store.store_number, 
            Store.state, 
            Store.city_name
        FROM Sold 
        JOIN Product ON Sold.PID = Product.PID 
        JOIN Store ON Sold.store_number = Store.store_number 
        JOIN District ON Store.district_number = District.district_number 
        JOIN ProductCategory ON Product.PID = ProductCategory.PID
        WHERE Sold.business_date BETWEEN %s AND %s 
        AND TRIM(District.district_number) = %s 
        AND ProductCategory.category_name = %s
        ORDER BY CAST(Store.store_number AS UNSIGNED) ASC;
        """

        cursor.execute(query, (start_date, end_date, district, category))
        sub_report = cursor.fetchall()
        cursor.close()
    except Exception as e:
        print(f"Error in sub_report: {e}")
        flash('An error occurred while generating the sub-report.', 'danger')

    return render_template('district_volume_details.html',
                           data=sub_report,
                           category=category,
                           district=district,
                           start_date=start_date,
                           end_date=end_date)

@app.route("/revenue_population")
def revenue_population():
    if not "logged_in"  in session : return redirect(url_for('login'))
    
    if not session.get('has_all_district', False):
        print(session.get('has_all_district'))
        return redirect('invalid.html')
        
    cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
    
    cursor.execute("""
        SELECT 
            CASE 
                WHEN City.population < 3700000 THEN 'Small'
                WHEN City.population >=3700000 AND City.population < 6700000 THEN 'Medium'
                WHEN City.population >= 6700000 AND City.population < 9000000 THEN 'Large'
                ELSE 'Extra Large' 
            END AS city_size,
            EXTRACT(YEAR FROM Sold.business_date) AS year,
            ROUND(SUM(
                    CASE 
                        WHEN Discount.discount_price IS NOT NULL THEN Sold.quantity * Discount.discount_price 
                        ELSE Sold.quantity * Product.retail_price 
                    END),2) AS total_revenue 
        FROM Sold 
        JOIN Store ON Store.store_number = Sold.store_number 
        JOIN Product ON Product.PID = Sold.PID 
        JOIN City ON City.state = Store.state 
        LEFT JOIN Discount ON Discount.business_date = Sold.business_date and Discount.PID = Product.PID
        GROUP BY city_size, year 
        ORDER BY (
                    CASE 
                        WHEN city_size = 'Small' THEN 1
                        WHEN city_size ='Medium' THEN 2
                        WHEN city_size ='Large' THEN 3 ELSE 4 END),year ASC;
        """)
        
    # Fetch all the results into a list of tuples (city_size, year, total_revenue)
    revenue_data= cursor.fetchall()
    # Create a dictionary to store data for each group (Small/Medium/Large/Extra Large)         
    grouped_data={}
         
    for row in revenue_data:    
        size=row['city_size']
        if not grouped_data.get(size):
            grouped_data[size]=[]
        grouped_data[size].append({'year':row['year'], 'total_revenue':row['total_revenue']})
        
       # Close the database connection    
    cursor.close()   
    flash({"data":grouped_data},"success")

    view_report_insert_log("Revenue by Population")
    return render_template('revenue_population.html')   
    

@app.route('/holiday')
def holiday():
    return render_template('holiday.html')

@app.route('/get_holidays', methods=['GET'])
def get_holidays():
    cursor = mysql.connection.cursor()
    cursor.execute("SELECT business_date, holiday_name, employeeID FROM Holiday ORDER BY holiday_name ASC")
    holidays = cursor.fetchall()
    cursor.close()
   
    formatted_holidays = [
        {
            "business_date": holiday[0].strftime('%Y-%m-%d'),  
            "holiday_name": holiday[1],
            "employeeID": holiday[2], 
        }
        for holiday in holidays
    ]
    
    return jsonify(formatted_holidays)

@app.route('/add_holiday', methods=['POST'])
def add_holiday():
    if not session.get('has_all_district', False):
        print(session.get('has_all_district'))
        return redirect('invalid.html') 
    data = request.get_json()
    business_date = data['business_date']
    holiday_name = data['holiday_name']
    employeeID = data['employeeID']
    
    cursor = mysql.connection.cursor()
    
    cursor.execute("SELECT business_date FROM BusinessDay WHERE business_date = %s", (business_date,))
    
    valid_business_date = cursor.fetchone()
    print('add_holiday function successfully received')
    
    if not valid_business_date:
        cursor.close()
        return jsonify({'success': False, 'message': 'Holiday must be a business date.'}), 400
    
    cursor.execute("SELECT * FROM Holiday WHERE business_date = %s", (business_date,))
    existing_holiday = cursor.fetchone()
    
    if existing_holiday:
        cursor.close()
        return jsonify({'success': False, 'message': 'Holiday already exists.'}), 400
    
    cursor.execute("INSERT INTO Holiday (business_date, holiday_name, employeeID, date_added) VALUES (%s, %s, %s, %s)", 
                   (business_date, holiday_name, employeeID, datetime.now()))
    mysql.connection.commit()
    cursor.close()
    print('add_holiday function successfully recorded')
    return jsonify({'success': True})

@app.route('/delete_holiday', methods=['POST'])
def delete_holiday():
    
    if not session.get('has_all_district', False):
        print(session.get('has_all_district'))
        return redirect('invalid.html') 
    
    try:
        data = request.get_json()
        business_date = data['business_date']
        
        cursor = mysql.connection.cursor()
        cursor.execute("DELETE FROM Holiday WHERE business_date = %s", (business_date,))
        mysql.connection.commit()
        
        # Debug
        print(f"Attempted to delete holiday with business_date: {business_date}, Rows affected: {cursor.rowcount}")
        
        cursor.close()
        
        if cursor.rowcount == 0:
            return jsonify({'success': False, 'message': 'Holiday not found'}), 404
        
        return jsonify({'success': True})
    except Exception as e:
        print("Error in delete_holiday:", e)  
        return jsonify({'success': False, 'message': str(e)}), 500


@app.route("/audit_log")
def audit_log():
    if not "logged_in"  in session : return redirect(url_for('login'))
    user_id = session.get('user_id')
    if not user_id:
        abort(403)

    cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
    cursor.execute("SELECT can_view_audit_log FROM User WHERE employeeID = %s", [user_id])
    permission = cursor.fetchone()
    if not permission or not permission['can_view_audit_log']:
        abort(403)

    query = """
        SELECT 
            al.time_stamp, 
            al.employeeID_view_report, 
            CONCAT(u.last_name, ', ', u.first_name) AS full_name, 
            al.report_name
        FROM 
            AuditLog al
        JOIN 
            User u ON al.employeeID_view_report = u.employeeID
        ORDER BY 
            al.time_stamp DESC, 
            al.employeeID_view_report ASC
        LIMIT 100
    """
    cursor.execute(query)
    audit_logs = cursor.fetchall()
    for log in audit_logs:
        log['is_assigned_all_areas'] = user_has_all_district(log['employeeID_view_report'])
        # print(f"Timestamp: {log['time_stamp']}, Employee ID: {log['employeeID_view_report']}, Full Name: {log['full_name']}, Report Name: {log['report_name']}, Assigned All Areas: {log['is_assigned_all_areas']}")

    cursor.close()
    return render_template('audit_log.html',data=audit_logs)

def view_report_insert_log(report_name):
    # print(report_name,report_name in session['report_names'] )
    if not report_name in session['report_names']:
        return False
    
    user_id = session.get('user_id')
    timestamp = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    result = False

    # print(user_id, timestamp, report_name)
    try:
        cursor = mysql.connection.cursor()
        cursor.execute(
            "INSERT INTO auditlog (employeeID_view_report, time_stamp, report_name) VALUES (%s, %s, %s)",
            (user_id, timestamp, report_name)
        )
        mysql.connection.commit()
    except MySQLdb.Error as e:
        print('view_report_insert_log failed')
        result= False
    finally:
        cursor.close()

    result= True
    return result


@app.route('/get_flash_messages', methods=[ "GET"])
def get_flash_messages():
    messages = get_flashed_messages(with_categories=True)
    return jsonify(messages)

@app.route("/login_user", methods=[ "POST"])
def login_user():
    req_data = request.get_json()

    if 'username' in req_data and 'password' in req_data:
        username = req_data['username']
        password = req_data['password']

        # print(username,password)
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute(
            "SELECT *  FROM `User` WHERE employeeID= % s",
            (username,),
        )
        user = cursor.fetchone()

        cursor.execute("SELECT * FROM report")
        report_names =cursor.fetchall()
        report_names = list(map(lambda x: x['report_name'], report_names))

        cursor.close()

        if user and str(user["last_ssn"])+"-"+user["last_name"].capitalize() == password:
            session["user_id"]  = str(user["employeeID"])
            session["logged_in"]  = True
            session['districts_assigned'] = get_assigned_district(user['employeeID'])
            session['has_all_district'] = user_has_all_district(user['employeeID'])
            session['can_view_audit_log'] = user["can_view_audit_log"]
            session['report_names'] = report_names

            # Debugging output
            # print(f"User's assigned districts: {assigned_districts}")
            # print(f"Total number of districts: {district_count}")
            # print(f"Has all districts: {has_all_district}")

            flash({"employeeID":str(user["employeeID"]), 
                   "last_name":user["last_name"],
                   "first_name":user["first_name"],
                   "can_view_audit_log":str(user["can_view_audit_log"]),
                   'has_all_district': session['has_all_district'],
                   'assigned_districts': session['districts_assigned'],
                #    'district_count': district_count
                   }, 
                   'success')
            return redirect(url_for('home', user_id=session["user_id"]))
        else:
            return jsonify({'message': 'Invalid credentials'}), 401
    return render_template("login.html")


if __name__ == "__main__":
    app.run()
    os.execv(__file__, sys.argv)
