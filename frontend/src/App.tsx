import React from "react";
import Interval from "./components/Interval/Interval";
import Table from "./components/Table/Table";
import MeasureProvider from "./contexts/MeasureContext";
import { tempMock } from "./temp/temp";

function App() {
  return (
    <MeasureProvider>
      <div className="App">
        <Table />
      </div>
    </MeasureProvider>
  );
}

export default App;
