import TurboLogger from '@mattermost/react-native-turbo-log';
import { AppRegistry } from 'react-native';
import App from './src/App';

TurboLogger.configure({
  dailyRolling: false,
  maximumFileSize: 1024 * 1024,
  maximumNumberOfFiles: 2,
});

AppRegistry.registerComponent('main', () => App);
