import React, {
  PropsWithChildren,
  createContext,
  useContext,
  useState,
} from "react";
import { DurationMeasuresFormats, HOUR_WIDTH } from "../constants";

const MeasureContext = createContext<{
  durationFormat: string;
  onChangeDurationFormat: (duration: string) => void;
  hourWidth: number;
  onChangeHourWidth: (width: number) => void;
}>({
  durationFormat: DurationMeasuresFormats["date"],
  onChangeDurationFormat: () => {},
  hourWidth: HOUR_WIDTH,
  onChangeHourWidth: () => {},
});

export const useMeasure = () => useContext(MeasureContext);

const MeasureProvider: React.FC<PropsWithChildren> = ({ children }) => {
  const [durationFormat, setDurationFormat] = useState<string>(
    DurationMeasuresFormats["date"]
  );
  const [hourWidth, setHourWidth] = useState(HOUR_WIDTH);

  return (
    <MeasureContext.Provider
      value={{
        durationFormat,
        onChangeDurationFormat: setDurationFormat,
        hourWidth,
        onChangeHourWidth: setHourWidth,
      }}
    >
      {children}
    </MeasureContext.Provider>
  );
};

export default MeasureProvider;
