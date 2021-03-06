### Librum

[![Build Status](https://www.bitrise.io/app/d0c12cb6f49a43ad/status.svg?token=SSvOfK1bhUhr8kPNr_E-DQ&branch=master)](https://www.bitrise.io/app/d0c12cb6f49a43ad)

Librum (Latin for Book) is an ePub reader and parser framework written in Kotlin.

This is a fork from [FolioReader](https://github.com/FolioReader/FolioReader-Android) with the aim of making the library easier to use and maintain. Most of the components have been re-written in accordance with an MVP structure in mind.

### Features

Most of the features will remain the same from the FolioReader library, with a couple more additions such as ability to read a book from a remote URL. This is the biggest inspiration of the library re-write.

- [x] Custom Fonts
- [x] Custom Text Size
- [x] Themes / Day mode / Night mode
- [x] Text Highlighting
- [x] List / Edit / Delete Highlights
- [x] Handle Internal and External Links
- [x] Portrait / Landscape
- [x] Reading Time Left / Pages left
- [ ] In-App Dictionary
- [x] Media Overlays (Sync text rendering with audio playback)
- [x] TTS - Text to Speech Support
- [ ] Parse epub cover image
- [ ] PDF support
- [ ] Book Search
- [x] Add Notes to a Highlight



## Demo
##### Custom Fonts
![Custom fonts](https://cloud.githubusercontent.com/assets/1277242/19012915/0661c7b2-87e0-11e6-81d6-8c71051e1074.gif)
##### Day and Night Mode
![Day night mode](https://cloud.githubusercontent.com/assets/1277242/19012914/f42059c4-87df-11e6-97f8-29e61a79e8aa.gif)
##### Text Highlighting 
![Highlight](https://cloud.githubusercontent.com/assets/1277242/19012904/c2700c3a-87df-11e6-97ed-507765b3ddf0.gif)
##### Media Overlays 
![Media Overlay](https://cloud.githubusercontent.com/assets/1277242/19012908/d61f3ce2-87df-11e6-8652-d72b6a1ad9a3.gif)

### Gradle
Add following dependency to your app build.gradle
``` java
compile 'com.librum:librum:0.4.1'
```

### Usage

To use Librum, you need to call LibrumActivity with following parameters:
1. INTENT_EPUB_SOURCE_TYPE - your epub can come from raw or assets folder or from SD card. Use enum LibrumActivity.EpubSourceType.
2. INTENT_EPUB_SOURCE_PATH - assets/SD card path of the epub file or raw ID of epub file if epub file is in raw folder

Reading from assets folder
```java
Intent intent = new Intent(HomeActivity.this, LibrumActivity.class);
intent.putExtra(INTENT_EPUB_SOURCE_TYPE, EpubSourceType.ASSESTS);
intent.putExtra(INTENT_EPUB_SOURCE_PATH, "epub/The Silver Chair.epub");
startActivity(intent);
```

Reading from raw folder of resources
```java
Intent intent = new Intent(HomeActivity.this, LibrumActivity.class);
intent.putExtra(INTENT_EPUB_SOURCE_TYPE, EpubSourceType.RAW);
intent.putExtra(INTENT_EPUB_SOURCE_PATH, R.raw.adventures);
startActivity(intent);
```

For reading from SD card, just retrieve absolute path of epub file and pass that in INTENT_EPUB_SOURCE_PATH.

### Credits
1. <a href="https://github.com/daimajia/AndroidSwipeLayout">SwipeLayout</a>
2. <a href="http://ormlite.com/">ORMLite</a>
3. <a href="https://github.com/julianharty/new-android-daisy-reader">SMIL parsing</a>

### Author

[**Brian Lusina**](https://github.com/BrianLusina)
