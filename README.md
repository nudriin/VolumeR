# VolumeR (Volume Overlay App)

**VolumeR** is a simple Android application that allows users to control their device volume directly through a draggable overlay menu, similar to Assistive Touch on iOS. The app utilizes a Foreground Service to ensure the overlay menu stays active on the screen.

---

## **Features**

1. **Floating Overlay Menu**:
   - The overlay menu is always displayed on the screen.
   - It can be moved to any position on the screen.
   
2. **Volume Control**:
   - **Volume Up**: Increases the device volume.
   - **Volume Down**: Decreases the device volume.
   - **Mute Toggle**: Toggles the device sound on/off.

3. **Assistive Touch Button**:
   - The main button is designed like Assistive Touch on iOS.
   - Displays additional menu options for volume control.

4. **Foreground Service**:
   - Ensures the app remains active in the background with a user-accessible notification.

---

## **Installation**

### **Prerequisites**
- Minimum Android SDK version: **21 (Lollipop)**
- Required permissions:
  - `SYSTEM_ALERT_WINDOW`
  - `FOREGROUND_SERVICE`

### **Steps**

1. Clone this repository:
   ```bash
   git clone https://github.com/nudriin/volume-overlay.git
   ```

2. Open the project in **Android Studio**.

3. Connect your Android device or use an emulator.

4. Run the app:
   ```
   Click the Run button (►) in Android Studio.
   ```

---

## **How to Use**

1. Upon first launch:
   - The app will request overlay permissions. Grant these permissions to allow the overlay menu to appear.

2. Once permissions are granted, the overlay menu will appear as a floating button on the screen.

3. Interacting with the overlay menu:
   - **Drag** the button to move it to a different position.
   - **Tap** the button to open the volume control menu.
   - Use the buttons in the menu to adjust volume:
     - **Volume Up**: Increase volume.
     - **Volume Down**: Decrease volume.
     - **Mute Toggle**: Turn the sound on/off.

4. To close the overlay menu, tap the **Close** button in the menu.

---

## **Project Structure**

### **Main Code**

- `MainActivity`: Checks for overlay permissions and starts the overlay service.
- `VolumeOverlayService`: Handles the overlay menu logic and volume control.

### **Layout**

- `overlay_layout.xml`: The design layout of the overlay menu.

### **Drawables**

- Icons for the buttons (e.g., `add`, `remove`, `mute`, `floating_icon`).

---

## **Permissions Used**

### **SYSTEM_ALERT_WINDOW**
Used to display the overlay menu on top of other apps.

### **FOREGROUND_SERVICE**
Ensures the overlay service keeps running in the background.

---

## **Additional Notes**

- Ensure your device allows the app to run in the background to keep the overlay menu active.
- The app is designed with a focus on efficiency and a minimalist design.

---

## **Future Development**

Potential additional features:

1. **Custom Shortcuts**:
   - Add the ability to map buttons to other functions, such as opening specific apps.

2. **Custom Themes**:
   - Provide options to change the theme or colors of the overlay menu.

3. **Media Player Integration**:
   - Control media playback functions like play, pause, and skip.

---


Created with ❤ by Nurdin.

