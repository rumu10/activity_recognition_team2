#User Stories:
<ol>
   <li> As a student user, I want to create an account and enter my account information (username and password), name, email, WPI ID,  major, GPA, and expected graduation date. </li>
   <li> As a student user, I want to select the courses I served as an SA before and save them in my profile.</li>
   <li> As a student user, I want to log in to my account using the username and password I provided when I created my account.</li>
   <li> As a student user, I want to log in to my account through WPI's SSO service.</li>
   <li> As a student user, I want to view all open SA positions.</li>
   <li> As a student user, I want to view the open SA positions that are recommended to me (i.e., that match my experience and qualifications).  </li>
   <li> As a student user, I want to view specific information about the SA positions including course number, course section, course title, term it is offered, instructor's name and contact information, and qalifications needed for the SA position.  </li>
   <li> As a student user, I want to apply for SA positions and provide information about when I took this particular course and what grade I earned.  </li>
   <li> As a student user, I want to view the SA positions I applied to and check their statuses, i.e., whether they are "pending" or "assigned".  </li>
   <li> As a student user, I want to withdraw one or more of my “pending” applications</li>
   <li> As a student user, I want to have "assigned"  SA positions be disabled for withdrawal  (i.e., I can't withdraw from "assigned" positions.  </li>

   <li> As a faculty user, I want to create an account and enter my account information (username and password), name, WPI email, WPI ID, and phone number). </li>
   <li> As a faculty user, I want to log in to my account using the username and password I provided when I created my account.</li>
   <li> As a student user, I want to log in to my account through WPI's SSO service.</li>
   <li> As a faculty user, I want to create undergraduate SA positions.</li> 
   <li> As a faculty user, I want to add a new course section that I will teach and specify the section  details course number, section number, and the term).</li>
   <li> As a faculty user, I want to create SA positions for my course sections and provide the number of SAs that will be hired and qualifications needed for the positions. </li>
   <li> As a Faculty, I would like to view the students who applied to a particular SA position. </li>
   <li> As a faculty user, I would like to view profile information for each applicant, including GPA, grades earned in the class, and courses they served as TA before.  </li>
   <li> As a Faculty, after interviewing a student, I would like to update the status of their application from “Pending” to "Assigned" so that I can hire them for the position.  </li>

</ol>


## Use cases:

1. Create an new course section (faculty)
2. Create an student assistant (SA) position (faculty)
    - (optional) View all self-created positions (faculty) 
3. View all SA positions (student)
    -  View recommended SA positions (student) 
    -  View details of an SA position (student) 
4. Apply for a SA position (student)
5. View applications for an SA position (faculty)
   - View applicant credentials (faculty) 
6. Update application status to "assigned"(faculty)
7. View all applications of the self (student) 
   - View status of application (student) 
8. Withdraw pending application (student)
 

Others 
1. Create an account (student, faculty)
2. Login to account (student, faculty)
3. Logout of an account (student, faculty)
4. Enter/update profile information (student, faculty)

| Use case # 1      |  |
| ------------------ |--|
| Name               | Create an course section |
| Participating Actors              | Faculty users |
| Entry condition(s)      | Faculty user is signed in and the faculty main page is loaded |
| Exit condition(s)     | A new course section is created and displayed among all of the sections the user has created.|
| Flow of events            | 1. User navigates to course section creation page <br> 2. User selects and enters the course section details (course major and number, section number, and the term) <br> 3. The system validates user's inputs <br> 4. The system creates the new course section and confirms the position was created |
| Alternative Path   | In step 3, validation fails and they're redirected to the same page with errors shown |


| Use case # 2      |  |
| ------------------ |--|
| Name               | Create a student assistant (SA) position |
| Participating Actors              | Faculty users |
| Entry condition(s)      | Faculty user is signed in and the faculty main page is loaded |
| Exit condition(s)     | A new SA position is created and displayed among all of the positions the user has created.|
| Flow of events            | 1. User navigates to SA position creation page <br> 2. User chooses the course section for theposition, enters the number of SAs needed, and the the qualifications needed for the position (min GPA required, min grade earned for the course, prior SA experience)  3. The system validates member's inputs <br> 4. The system creates the SA position and confirms the position was created |
| Alternative Path   | In step 3, validation fails and they're redirected to the same page with errors shown |

| Use case # 3      |  |
| ------------------ |--|
| Name              | View all SA positions |
| Participating Actors             | Student users |
| Entry condition(s)     | Student is signed in and the student page is loaded |
| Exit condition(s)    | The open SA positions are displayed. The recommended positions based on the user’s "past SA experience" and "qualifications" are identified. |
| Flow of events           |1. Student navigates to student home page. <br> 2. The system displays the list of all open SA positions |
| Alternative flow of events | 1. In step 1, the user may choose the “Recommended SA positions” option. Then, the systems will filter the positions that match the student's own SA experience and qualifications, and displays those as recommended positions.  <br> After step2, the user may choose one of the SA positions and choose the view option. The systems will display the details of that position including the number, section, and title of the course section, the term it will be offered, instructor’s name and contact information, and qualifications needed for the SA position. <br> 2. In step2, if the student already applied to the position, the systems will display the status of the applications and the "Apply" option for that position will be disabled. For all other positions, Apply option will be available. 


| Use case # 4      |  |
| ------------------ |--|
| Name              | Apply for an SA position  |
| Participating Actors             | "Student users."  |
| Entry condition(s)     | The user logged into the system and the open positions are displayed on the student main page.  |
| Exit condition(s)    | The user’s application is submitted and the SA position is added to the list of ‘pending’ applications. |
| Flow of events           | "1. The student selects an SA position that is posted.<br>2. The software displays the position information and the option for the student to apply.<br>3. The student selects “Apply for position” option for the position. <br> 4. The system prompts the user for the grade they earned when they took the course , the year and term they took the course , the year and term they are applying for SAship.<br>  5. The user enters the information <br>  6. The system creates the application and and acknowledges that the application is created. <br>7. The systems labels the position as applied.   |
| Alternative flow of events | None  |


| Use case # 5      |  |
| ------------------ |--|
| Name              | View applications for a position  |
| Participating Actors             | Faculty Users  |
| Entry condition(s)     | Student(s) have applied for the SA position. The faculty main page is loaded and the positions of the faculty are displayed.  |
| Exit condition(s)    | List of the student applications for the position and information about whether the students are "approved"  for other positions are displayed to the faculty user.  |
| Flow of events           | 1. The user selects a position and chooses the “View Student Applications” option for the position.<br>2. The software displays the list of the students who applied to the position.   |
| Alternative flow of events | 1. After step 2, the user may choose display the profile information of a student. The system will display the GPA, grades earned in their classes, and courses they served as SA before.  |
| Iteration         | Iteration 2  |


| Use case # 6      |   |
| ------------------ |--|
| Name              | Update application status for a student |
| Users             | Faculty Users  |
| Entry condition(s)     | The user logged into the  system and the information of the student who applied to the position is displayed. The application status of the student appears as "pending." |
| Exit condition(s)    | The system changes the application status of the selected student. The number of available SA positions for the course assigned should be updated (decreased by 1)  |
| Flow of events           | 1. The user selects the "Approve" option for the application of the student (with status) ‘pending’. <br> 2. The system asks confirmation from the user. <br> 3. The user confirms that they want to approve the application. <br> 4. System assigns the student as an SA for the position. It updates the application status of the student as Assigned.   |
| Alternative flow of events | 1. In step 3, the user can cancel the approval and terminate the use case.  |



| Use case # 7      |  |
| ------------------ |--|
| Name              | View all applications of the self  |
| Participating Actors             | Student Users  |
| Entry condition(s)     | The user logged into the  system and the student main page is loaded |
| Exit condition(s)    | The SA positions the user has submitted applications to are displayed with the current status of student’s application  |
| Flow of events           | 1. The student selects the option to view their current applied positions.<br>2. The system displays a list of the SA positions the user has applied to and displays the status of each application.
| Alternative flow of events |   |


| Use case # 8     |  |
| ------------------ |--|
| Name              | Withdraw pending application |
| Participating Actors             | Student Users  |
| Entry condition(s)     | The user logged into the  system and the page that displays student's applications are open. The status of the application that needs to be deleted is "pending".   |
| Exit condition(s)    | User's application for the selected position is removed in the system.  |
| Flow of events           | 1. The user chooses the ‘withdraw’ option for the application. <br> 2. The system prompts the user for confirmation to withdraw their application to the specific SA position. <br> 3. The user confirms their withdraw action. <br> 4. The system deletes the application and acknowledges the user that it is deleted. <br> 5. The system updates the page and the deleted application is excluded.  |
| Alternative flow of events |  |


