# Halalify: Automated Video Profanity Filter
Halalify is an innovative app that uses the IBM API to transcribe audio from videos into text. It then compares the transcribed text to a customizable list of bad words and automatically censors them, ensuring a cleaner, family-friendly viewing experience.

#How to use:

-Download FFMPEG: https://www.gyan.dev/ffmpeg/builds/ffmpeg-git-essentials.7z

-Download JavaFX: https://download2.gluonhq.com/openjfx/23.0.1/openjfx-23.0.1_windows-x64_bin-sdk.zip

After running:

-Select the desired video.

-Select the path to the ffmpeg.exe file.

-Select the output directory.

-Select Mute or Bleep.


#Disclosure:

-Use of Generative AI in Development

The Halalify app's development involved the assistance of generative AI (genAI) to enhance productivity, improve code quality, and expedite problem-solving. Below, we outline how generative AI was used in the project:

-Purpose of AI Assistance

Generative AI was employed to:

1. Design and refine specific Java classes, including filters for audio processing (e.g., BleepFilter, MuteFilter).
2. Debug and troubleshoot issues in the code, such as ensuring proper insertion of bleep sounds and handling audio streams correctly.
3. Provide explanations of complex programming concepts and suggest improvements to logic or structure.
4. Clarify functionality for specific features, such as handling timestamps for profane audio and implementing audio skipping or muting.
5. Iterate solutions based on observed behavior in the output, as described through prompts.

-Example Prompts Used

Here are some examples of prompts provided to the AI during the development process:

1. Initial Class Design:

"Can you help me design a Java class called BleepFilter that replaces profanity in audio with a bleep sound?"

2. Debugging:

"The audio of the profanity is just being moved 1 second forward instead of putting a bleep on top of it."
"When using the system, in the outputted video it adds a weird beep that doesn't sound like the beep I added"

3. Explaining Behavior:

"yeah the problem comes right after "transcribing audio", so does it have to do with IBM API?"
"When using the system, in the outputted video it adds a weird beep that doesn't sound like the beep I added"

4. Improving Logic:

"Does it make sense that the merge call gives videopath instead of audiopath?"
"The question is, is this class good to have? or should GUI.java replace it? again I want the best practice"

5. Error Handling:

"there's something wrong with the mute filter because the output just stops and the window says "not responding" then I have to terminate it manually."

-Role of Human Oversight

While generative AI provided valuable guidance and code suggestions, all AI-generated content was reviewed, tested, and adapted by the development team to ensure functionality, correctness, and alignment with project goals. The final implementation reflects a combination of AI-generated insights and human expertise.

-Ethical Use of AI

This project adheres to principles of transparency and ethical use of generative AI. By disclosing its role in the development process, we aim to provide full clarity regarding the use of AI in the Halalify app.

