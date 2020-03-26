# Changelog
All notable changes to this project will be documented in this file.

## [Unreleased]

## [v1.14] 2020-03-26
### Added
- Brazilian Portuguese translation (thanks to Ivan Amaro)
### Changed
- Internal representation of high score data

## [v1.13] 2020-03-10
### Added
- Night mode support
- Settings screen
- Option to reset hall of fame

### Fixed
- Hall of fame not loading

## [v1.12] 2019-08-09
### Added
- Actionbar "up" button navigation

### Changed
- Converted "screens" from fragments to activities

### Fixed
- Crash after rotating while news report is open (issue #62)

## [v1.11] 2019-03-26
### Added
- Fastlane metadata

### Changed
- Updated French translation

### Fixed
- Typos in English and French translations

## [v1.10] - 2018-10-26
### Added
- News list button
- News list screen

### Changed
- Button images for "+" and "-"
- Report text format on game screen
- Demon attack event notification more prominent

### Fixed
- Crash in main menu after orientation change (issue #50)

## [v1.9] - 2018-04-23
### Added
- Debug logging
- Adaptive icon

### Changed
- Updated german translation
- Untranslated entries added as comments to make it more visible what is missing

## [v1.8] - 2017-11-02
### Added
- More German translation

### Changed
- Hall of Fame entries from v1.7 are deleted
- Turn, population and wall status remains displayed when the is game over
- Population distribution buttons removed when the is game over

### Fixed
- Hall of Fame was logging defeats


## [v1.7] - 2017-10-10
### Added
- Round icon
- Demon strength per unit grows exponentially over time
- Fastest win tracking
- Hall of Fame screen

### Changed
- Banishment ritual time estimation again but more precisely then before v1.6
- Banishment ritual progress now shows true goal progress instead of attempts to close demon gate
- Few entries in French translation


## [v1.6] - 2017-08-26
### Added
- Difficulty level info when starting new game
- Hell gate opening and closing report during banishment ritual

### Changed
- Banishment ritual shows remaining hell gates instead of time estimation
- Increased item size in research selection
- German translation


## [v1.5] - 2017-07-29
### Changed
- Launcher icon

### Fixed
- Main menu duplication after phone rotation


## [v1.4] - 2017-07-15
### Fixed
- Lint error stopping build in F-Droid


## [v1.3] - 2017-07-12
### Added
- Change log file
- French translation (thanks to Julien Lepiller)
- Difficulty levels

### Fixed
- Back button always navigates from other views to main menu


## [v1.2] - 2017-06-21
### Added
- Autosave and continue game session
- German translation (thanks to mueller-ma)

### Changed
- Moderate code cleanup
- Reduced APK size
- About screen lists translators

### Fixed
- Fixed licence statement, GLP text included too
- Fixed defeat condition to work with decimal population count


## [v1.1] - 2017-06-04
### Added
- Current game turn is now visible

### Changed
- "Help" screen is now more detailed "Story & Help"

### Fixed
- Fixed UI scaling and duplication on rotation
- Fixed loss of game state on rotation or multitasking
- Fixed UI duplication after pressing back button
- Fixed negative scout report


## [v1.0] - 2017-05-27
Initial release
