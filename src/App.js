import React from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import Sidebar from './component/Sidebar';
import Header from './component/Header';
import Probes from './component/Probes';
import ProbeDetails from './component/ProbeDetails';
import Module from './component/Module';
import ModuleHistory from './component/ModuleHistory';
import { ProbesProvider } from "./component/ProbesTable/ProbesContext";
function App() {
  return (
    <div className="App" style={{ padding: 0, margin: 0, height: "100vh", backgroundColor: "#0b0c24", overflowY: "hidden" }}>
      <div className='headerContainer' style={{ height: "80px" }}>
        <Header></Header>
      </div>
      <main className='mainContainer d-flex' style={{ backgroundColor: "#0b0c24" }} >
        <div className='sidebarContainer' style={{ width: "10%", backgroundColor: "transparent", height: "100%" }}>
          <Sidebar></Sidebar>
        </div>
        <div className='dataContainer' style={{ width: "90%", backgroundColor: "transparent", height: "100%", padding: "0 30px" }} >
          {/* <ProbesProvider>
          <Probes></Probes>
        </ProbesProvider> */}
          <ProbeDetails></ProbeDetails>
          {/* <Module></Module> */}
          {/* <ModuleHistory></ModuleHistory> */}

        </div>
      </main>
    </div>
  );
}
export default App;
