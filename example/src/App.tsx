import TurboLogger, { LogLevel } from '@mattermost/react-native-turbo-log';
import * as React from 'react';
import { StyleSheet, View, Text } from 'react-native';

export default function App() {
  const [result, setResult] = React.useState<string | undefined>();

  React.useEffect(() => {
    setTimeout(() => {
      console.log('hijacking console log', 'a string second time', true, 10, {
        name: 'Someone',
        username: 'test-account',
      });
      console.info('only one line');
      console.debug('debug log');
      console.warn('this is a warning');
      console.error('this is an error');
      TurboLogger.log(LogLevel.Debug, 'Log directly without console');
      TurboLogger.getLogPaths().then((files) => {
        setResult(files.join('\n'));
      });
    });
  }, []);

  return (
    <View style={styles.container}>
      <Text onPress={TurboLogger.deleteLogs}>Log files:</Text>
      <Text selectable={true} selectionColor="blue">
        {result}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingHorizontal: 20,
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
