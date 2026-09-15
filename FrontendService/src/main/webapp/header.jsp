<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Salary Payout System</title>
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary: #0052cc;
            --primary-hover: #0047b3;
            --secondary: #00875a;
            --bg-color: #f4f5f7;
            --card-bg: #ffffff;
            --text-main: #172b4d;
            --text-muted: #5e6c84;
            --border-color: #dfe1e6;
            --danger: #de350b;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
        }

        body {
            background-color: var(--bg-color);
            color: var(--text-main);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        /* Strict Solid Navbar */
        nav {
            background-color: #172b4d;
            border-bottom: 2px solid #091e42;
            padding: 0.75rem 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            position: sticky;
            top: 0;
            z-index: 100;
        }

        .logo {
            font-size: 1.25rem;
            font-weight: bold;
            color: #ffffff;
            text-decoration: none;
            letter-spacing: 0.5px;
        }

        .nav-links {
            display: flex;
            gap: 1.5rem;
        }

        .nav-links a {
            color: #b3bac5;
            text-decoration: none;
            font-weight: 500;
            font-size: 0.9rem;
            padding: 0.5rem;
        }

        .nav-links a:hover, .nav-links a.active {
            color: #ffffff;
            text-decoration: underline;
        }

        /* Container & Strict Cards */
        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 1rem;
            flex-grow: 1;
            width: 100%;
        }

        .card {
            background: var(--card-bg);
            border: 1px solid var(--border-color);
            border-radius: 2px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
        }

        /* Typography */
        h1, h2, h3 {
            margin-bottom: 1rem;
            color: var(--text-main);
        }

        .page-title {
            font-size: 2rem;
            margin-bottom: 1.5rem;
            border-bottom: 1px solid var(--border-color);
            padding-bottom: 0.5rem;
        }

        /* Forms & Buttons */
        .form-group {
            margin-bottom: 1rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.25rem;
            color: var(--text-main);
            font-weight: bold;
            font-size: 0.85rem;
        }

        input, select {
            width: 100%;
            padding: 0.5rem;
            background: #ffffff;
            border: 1px solid var(--border-color);
            border-radius: 2px;
            color: var(--text-main);
            font-size: 0.9rem;
        }

        input:focus, select:focus {
            outline: none;
            border-color: var(--primary);
        }

        .btn {
            padding: 0.5rem 1rem;
            border: 1px solid transparent;
            border-radius: 2px;
            font-weight: bold;
            font-size: 0.9rem;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }

        .btn-primary {
            background-color: var(--primary);
            color: #ffffff;
        }

        .btn-primary:hover {
            background-color: var(--primary-hover);
        }

        .btn-success {
            background-color: var(--secondary);
            color: #ffffff;
        }

        .btn-success:hover {
            background-color: #006644;
        }

        /* Tables */
        .table-container {
            overflow-x: auto;
            border: 1px solid var(--border-color);
            background: var(--card-bg);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
        }

        th, td {
            padding: 0.75rem 1rem;
            border-bottom: 1px solid var(--border-color);
            font-size: 0.9rem;
        }

        th {
            background: #fafbfc;
            font-weight: bold;
            color: var(--text-muted);
            border-bottom: 2px solid var(--border-color);
        }

        tr:last-child td {
            border-bottom: none;
        }

        tr:nth-child(even) {
            background-color: #fafbfc;
        }
        
        .badge {
            padding: 0.15rem 0.5rem;
            border-radius: 2px;
            font-size: 0.75rem;
            font-weight: bold;
            border: 1px solid var(--border-color);
        }
        .badge-success { background: #e3fcef; color: #006644; border-color: #006644; }
        .badge-warning { background: #fffae6; color: #ff8b00; border-color: #ff8b00; }
        
        /* Grid Layouts */
        .grid-2 {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1.5rem;
        }

        .module-list {
            display: flex;
            flex-direction: column;
            gap: 1rem;
            max-width: 600px;
        }

        .module-item {
            display: block;
            background: var(--card-bg);
            border: 1px solid var(--border-color);
            padding: 1.5rem;
            text-decoration: none;
            color: var(--text-main);
            border-left: 4px solid var(--primary);
        }

        .module-item:hover {
            background: #fafbfc;
        }

        .module-item h3 {
            margin-bottom: 0.25rem;
            color: var(--primary);
        }
        
        .toast {
            position: fixed;
            bottom: 20px;
            right: 20px;
            background: var(--card-bg);
            border: 1px solid #000000;
            padding: 1rem;
            display: none;
            align-items: center;
            gap: 0.75rem;
            box-shadow: 2px 2px 0px rgba(0,0,0,0.2);
        }
        
        .toast.show {
            display: flex;
        }
        .toast.error { border-left: 4px solid var(--danger); }
        .toast.success { border-left: 4px solid var(--secondary); }
    </style>
</head>
<body>
    <nav>
        <a href="/" class="logo">SalaryCore Enterprise</a>
        <div class="nav-links">
            <a href="/" id="nav-home">Dashboard</a>
            <a href="/employees" id="nav-employees">Employees</a>
            <a href="/salaries" id="nav-salaries">Salaries</a>
            <a href="/payments" id="nav-payments">Payments</a>
        </div>
        <div style="display: flex; align-items: center; gap: 1rem;">
            <span style="color: #b3bac5; font-size: 0.85rem; font-weight: bold;">User: ${pageContext.request.userPrincipal != null ? pageContext.request.userPrincipal.name : 'Unknown'}</span>
            <form action="/logout" method="post" style="margin: 0;">
                <button type="submit" class="btn btn-primary" style="padding: 0.25rem 0.75rem; font-size: 0.8rem;">Logout</button>
            </form>
        </div>
    </nav>
    <div class="container">
