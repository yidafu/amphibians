import {TurboModule, TurboModuleRegistry} from 'react-native';

export interface Spec extends TurboModule {
  add(a: number, b: number): number;

  argumentExample(
    bool: boolean,
    byte: number,
    float: number,
    int: number,
    long: number,
    number: number,
    short: number,
    char: string,
    string: string,
    arrayInt: Array<number>,
    // map: Map<string, string>,
    data: {foo: string; bar: number},
  ): void;
}

/**
 * trigger named import */
type r = TurboModuleRegistry;

export default TurboModuleRegistry.get<Spec>('SimpleModuleAndroid');
