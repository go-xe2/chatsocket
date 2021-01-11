
# react-native-chatsocket

## Getting started

`$ npm install react-native-chatsocket --save`

### Mostly automatic installation

`$ react-native link react-native-chatsocket`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-chatsocket` and add `RNChatsocket.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNChatsocket.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.mnyun.chatsocket.RNChatsocketPackage;` to the imports at the top of the file
  - Add `new RNChatsocketPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-chatsocket'
  	project(':react-native-chatsocket').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-chatsocket/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-chatsocket')
  	```


## Usage
```javascript
import RNChatsocket from 'react-native-chatsocket';

// TODO: What to do with the module?
RNChatsocket;
```
  