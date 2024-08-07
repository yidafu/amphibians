import { TurboModule,TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
    // your module methods go here, for example:
    add(a: number, b: number): Promise<number>;
}

export default TurboModuleRegistry.get<Spec>("RTNCalculator") as Spec | null;
