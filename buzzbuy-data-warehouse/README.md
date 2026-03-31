# A team-based retail data warehouse project focused on schema design, role-based reporting, and business-oriented analytics.


# BuzzBuy Data Warehouse

This repository contains a team project focused on the design and implementation of a retail data warehouse for business reporting and decision support. The project was built around a fictional nationwide retail company, BuzzBuy, with the goal of consolidating operational data into a structured warehouse that supports analytics, reporting, and role-based access control.

## Project Overview

The project centered on designing a normalized data warehouse schema and a functional user interface for a retail business operating stores across the United States. The system was designed to support enterprise-style reporting on stores, products, manufacturers, categories, discounts, holidays, and sales activity, while also enforcing user-level access restrictions based on district assignments.

In addition to warehouse schema design, the project included report query development, user authentication logic, audit logging, and administrative workflows such as holiday management.

## Why This Project Matters

This project goes beyond basic database implementation and demonstrates how data modeling, access control, and analytical reporting can be integrated into a business-oriented system. It reflects practical database engineering considerations such as schema normalization, minimizing redundancy, supporting complex aggregation queries, and building application logic around user permissions and report access.

## Key Highlights

- Designed a normalized retail data warehouse schema with low redundancy
- Modeled stores, districts, cities, products, manufacturers, categories, discounts, holidays, users, permissions, and audit logs
- Implemented role-aware access logic based on district assignment
- Developed multiple business reporting queries for executive and operational decision support
- Supported drill-down reporting workflows for more detailed analysis
- Designed a user interface for login, report access, holiday management, and audit-log review
- Completed as a team project with collaborative design and implementation

## Core Functional Scope

The system was designed to support:

- User authentication with employee-based credentials
- District-based data access restrictions
- Holiday creation and viewing workflows
- Audit logging for report access
- General, district-level, and corporate-level reporting

The reporting layer included both summary and drill-down analytics for product, category, revenue, discount-performance, and population-based business analysis.

## Representative Reports

Examples of supported reports include:

- Manufacturer product summary with drill-down to product details
- Category-level product and pricing summary
- Actual versus predicted revenue for discounted GPS products
- Groundhog Day air-conditioner sales analysis by year
- Store revenue by year and state
- District with highest sales volume for each category by month
- Revenue trends by city population group

These reports were designed to reflect realistic business questions and demonstrate SQL-driven analytics over a warehouse-oriented schema.

## Team Project Note

This repository represents a **team project**. The uploaded materials are intended to showcase the overall system design, database thinking, analytical reporting workflow, and implementation structure. The repository is presented here as part of a selected project portfolio.

## Notes

- This repository is shared for project demonstration and portfolio purposes
- Some setup details may depend on the local database environment and configuration
- Large generated files, local credentials, and environment-specific artifacts should not be committed

