
# Mess Feedback App

## Project Overview
The Mess Feedback App is designed to streamline the communication and resolution process between students, mess representatives, contractors, and administrators in a university mess system. It provides a structured platform for students to share feedback, register complaints, and receive announcements. The app also facilitates efficient complaint validation, resolution tracking, and updates.

---

## Tech Stack
### **Frontend**
- Android (Kotlin)
- XML Layouts
- Material Design Components
- AndroidX Libraries
- ViewBinding

### **Backend**
- Firebase Realtime Database
- Firebase Storage
- Firebase Authentication

### **Libraries**
- Navigation Component
- Coroutines for asynchronous operations
- Lifecycle Components
- RecyclerView for list displays

---

## Setup and Installation

### **1. Clone the Repository**
```bash
git clone <repository_url>
cd <repository_name>
```

### **2. Firebase Setup**
- Create a new Firebase project on the [Firebase Console](https://console.firebase.google.com/).
- Add your Android app to the Firebase project.
- Download the `google-services.json` file and place it in the `app/` directory.
- Enable the following Firebase features:
  - **Authentication**
  - **Realtime Database**
  - **Firebase Storage**

### **3. Configure Database Rules**
#### Realtime Database Rules
```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```
#### Storage Rules
```json
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### **4. Install Dependencies**
Add the following dependencies to your app's `build.gradle` file:
```gradle
dependencies {
    implementation platform('com.google.firebase:firebase-bom:32.1.1')
    implementation 'com.google.firebase:firebase-auth-ktx'
    implementation 'com.google.firebase:firebase-database-ktx'
    implementation 'com.google.firebase:firebase-storage-ktx'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.6.2'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.4'
    implementation 'androidx.navigation:navigation-ui-ktx:2.7.4'
    implementation 'com.google.android.material:material:1.10.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    implementation 'androidx.recyclerview:recyclerview:1.3.1'
}
```

### **5. Sync and Run**
- Sync your project with Gradle files.
- Build and run the app to ensure proper setup.

---

## Key Features
1. **Student App**
   - View menu, give feedback, and register complaints.
   - Receive mess-related announcements.
   - Mess representatives can validate complaints and update statuses.

2. **Admin App**
   - Monitor complaints and feedback.
   - Take action on unresolved complaints.
   - Publish announcements and update complaint statuses.

3. **Contractor App**
   - Address complaints and update their statuses.
   - Notify students upon resolving issues.

---

## API Endpoints
### **Authentication**
- `POST /api/auth/login` - User login.
- `POST /api/auth/register` - User registration.

### **User Management**
- `GET /api/users` - Fetch all users.
- `GET /api/users/:id` - Fetch a specific user.
- `PUT /api/users/:id` - Update user details.
- `DELETE /api/users/:id` - Delete a user.

### **Menu Management**
- `GET /api/menu` - Retrieve the current menu.
- `POST /api/menu` - Add a new menu.
- `PUT /api/menu/:id` - Update a menu item.
- `DELETE /api/menu/:id` - Remove a menu item.

### **Complaints**
- `GET /api/complaints` - Get all complaints.
- `POST /api/complaints` - Register a complaint.
- `PUT /api/complaints/:id` - Update a complaint.
- `DELETE /api/complaints/:id` - Delete a complaint.

### **Announcements**
- `GET /api/announcements` - Get all announcements.
- `POST /api/announcements` - Create an announcement.
- `PUT /api/announcements/:id` - Update an announcement.
- `DELETE /api/announcements/:id` - Delete an announcement.

### **Image Upload**
- `POST /api/upload/image` - Upload an image.

---

## Team Members
- **K. Tanmayi**
- **S.V. Bindu Sathwika**
- **M. Karthik**
- **D. Maheswari**

### From RGUKT Nuzvid

---

Feel free to contribute, suggest enhancements, or report issues via GitHub.

