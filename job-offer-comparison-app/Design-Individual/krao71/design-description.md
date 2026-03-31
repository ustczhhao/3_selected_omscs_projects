# Design Description for Job Offer Comparison App

## Introduction

This document outlines the design of the job offer comparison app and explains how each part helps manage and compare job offers effectively.

## Overview of Each Class

### JobComparisonApp

The JobComparisonApp is the starting point of our application. It allows user to choose to enter new job details, review existing offers, modify comparison criteria, or start a job comparison. This central component ensures easy navigation and quick access to all functionalities of the app, making it user-friendly and efficient.

### JobManager

The JobManager handles all the job data user might want to explore or compare. Here, user can add new job offers or update and review details about jobs that user has already entered. It organizes and stores all job-related information, allowing for easy management and retrieval.

### ComparisonSettings

ComparisonSettings lets user adjust how much importance user give to different aspects of a job, such as salary, bonuses, and other benefits. By setting these preferences, user filters the job comparison process to focus on what matters most, ensuring that the app provides results that are relevant and useful in making informed decisions.

### Job

The Job class represents all the detailed information about a job, such as the job title, the company name, the job location, salary details, bonuses, and other benefits. This comprehensive collection of job attributes is important for comparing different job offers based on your selected criteria.

### ComparisonResult

ComparisonResult displays the outcomes of user job comparisons. It simplifies decision-making by clearly showing which job offer best matches and personalized settings and preferences. This component is helpful for visualizing the comparison results.

## How the App Works

When user start the app, the JobComparisonApp component allows user to navigate through different options like entering job details or comparing them. Job details are managed within the JobManager, where all data is stored and maintained. The ComparisonSettings component is where user set his/her preferences for what factors are most important in  job search. These settings are then used by the JobManager to compare different jobs, and the results are displayed by the ComparisonResult component.

