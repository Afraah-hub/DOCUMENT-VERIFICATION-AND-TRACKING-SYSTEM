# Simple Document Verification System (SimpleDVS)

## Overview

SimpleDVS is a standalone Java Swing application designed to demonstrate the fundamental principles of digital document verification and fraud detection.

The system operates on a "Claim vs. Reality" model. It allows an uploader to register a digital claim about a document's ownership. Subsequently, a separate verifier can validate that claim by inputting the details observed on the actual, physical document. The system detects fraud by comparing the original digital claim against physical reality.

**Key Highlights:**
* **Zero Dependencies:** Runs entirely on standard Java; no external libraries (like ZXing) are required.
* **Educational Simulation:** Perfect for understanding how identity and document mismatch fraud is detected logically.
* **In-Memory Database:** Uses a temporary internal data structure for simplicity (data resets when the application closes).

---

## Features

1.  **Role-Based Workflow:** Clear separation between the "Uploader" (who makes the claim) and the "Verifier" (who checks reality).
2.  **Signature ID Generation:** Automatically generates a unique UUID acting as a digital signature for tracking documents.
3.  **Strict Fraud Detection Engine:** Simultaneously checks for:
    * **File Tampering:** Does the file being held match the registered filename?
    * **Identity Fraud:** Does the name printed on the document match the claimed owner's name?
4.  **Detailed Result Reporting:** Provides clear, color-coded feedback detailing exactly why verification passed or failed (e.g., "Owner Name Mismatch").
5.  **Real-Time Tracking:** Check the current status of any document using its Signature ID.

---

## Prerequisites

* **Java Development Kit (JDK):** Version 8 or higher installed on your machine.
* **IDE (Optional but Recommended):** Eclipse, IntelliJ IDEA, or NetBeans for easy compilation and running.

---

## Setup and Installation

Since this project is a single-file, standalone application, setup is minimal.

### Option 1: Using Eclipse IDE (Recommended)

1.  Create a new Java Project in Eclipse.
2.  In the `src` folder, create a new class named exactly: **`SimpleDVS_Final`**
3.  Paste the entire source code into this file.
4.  Save the file. No external JARs are needed.
5.  Right-click the file and select **Run As > Java Application**.

### Option 2: Using Command Line

1.  Save the source code into a file named `SimpleDVS_Final.java`.
2.  Open a terminal or command prompt in the folder containing the file.
3.  Compile the code:
    ```bash
    javac SimpleDVS_Final.java
    ```
4.  Run the application:
    ```bash
    java SimpleDVS_Final
    ```

---

## Usage Guide (How to Simulate Fraud Detection)

To understand the system's power, you must simulate a scenario where the uploader lies, and the verifier catches them.

### Scenario: The Fraud Attempt

Imagine you have a friend's certificate named `FriendCert.pdf`, and the name printed on it is **"John Smith"**. You want to claim it belongs to **"My Name"**.

#### Step 1: Tab 1 - Making the False Claim (The Uploader)

1.  Navigate to the **"1. Make a Claim"** tab.
2.  **Claimed Owner Name:** Enter the lie: `My Name`.
3.  **Select File:** Browse and select the actual file: `FriendCert.pdf`.
4.  Click **Record this Claim**.
5.  *The system now stores: ID XYZ maps to "My Name" and "FriendCert.pdf".*
6.  **Copy** the generated Signature ID.

#### Step 2: Tab 2 - The Reality Check (The Verifier)

Now act as an honest official holding the physical document.

1.  Navigate to the **"2. Validate Claim"** tab.
2.  **Paste Signature ID:** Paste the ID from Step 1.
3.  **Select File you hold:** Browse and select the actual file: `FriendCert.pdf`.
4.  **Name ON Document:** Look at the paper. It says "John Smith". Type the truth: `John Smith`.
5.  Click **Validate the Claim**.

**Result:** The system compares the claim ("My Name") against reality ("John Smith") and correctly reports: **⚠️ FRAUD DETECTED / OWNER NAME MISMATCH**.

---

## Technical Details

* **GUI Framework:** Java Swing (`JFrame`, `JTabbedPane`, `JPanel`, etc.).
* **Data Storage:** `static Map<String, DocumentClaim> db = new HashMap<>();`
    * This in-memory map stores data only while the program is running.
* **Core Logic:** String comparison logic located in the `ActionListener` of the "Validate" button in Tab 2. It compares stored objects from the HashMap against realtime text field inputs.

## Limitations

* **No Optical Character Recognition (OCR):** The system does not physically "read" the text inside the PDF or image file. It relies on the verifier truthfully typing what they see on the document into the input field in Tab 2.
* **Non-Persistent Data:** All registered claims are lost immediately when the application window is closed.
