import React from "react";
import Table from "./components/Table/Table";
import MeasureProvider from "./contexts/MeasureContext";
import { SWRConfig } from "swr";

function App() {
  return (
    <MeasureProvider>
      <SWRConfig>
        <div className="App">
          <Table />
        </div>
      </SWRConfig>
    </MeasureProvider>
  );
}

export default App;
