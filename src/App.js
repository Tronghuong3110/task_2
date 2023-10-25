import React from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import Sidebar from './component/Layout/Sidebar';
import Header from './component/Layout/Header';
import Probes from './component/Probes/Probes';
import ProbeDetails from './component/ProbeDetails/ProbeDetails';
import Footer from './component/Layout/Footer';
import Module from './component/Modules/Module';
// import ModuleHistory from './component/ModuleHistory';
import { ProbesProvider } from "./component/Probes/ProbesTable/ProbesContext";
import {Routes,Route} from 'react-router-dom'
function App() {
  return (
    <div className="App" style={{ padding: 0, margin: 0, backgroundColor: "#0b0c24"}}>
      <div className='headerContainer' style={{ height: "80px" }}>
        <Header></Header>
      </div>
      <main className='mainContainer d-flex' style={{ backgroundColor: "#0b0c24" }} >
        <div className='sidebarContainer' style={{ width: "10%", backgroundColor: "transparent", height: "100%" }}>
          <Sidebar></Sidebar>
        </div>
        <div className='dataContainer' style={{ width: "90%", backgroundColor: "transparent", padding: "0 30px" }} >
          {/* <ProbesProvider>
            <Probes></Probes>
          </ProbesProvider> */}
          {/* <Module></Module> */}
          {/* <ModuleHistory></ModuleHistory> */}
          <Routes>
            <Route path='/' element={<ProbesProvider><Probes></Probes></ProbesProvider>}></Route>
            <Route path='/details/:id' element={<ProbeDetails></ProbeDetails>}></Route>
            <Route path='/modules' element={<Module></Module>}></Route>
            {/* <Route path='/process' element={<Process />}></Route> */}
          </Routes>
        </div>
      </main>
      <div className='footerContainer' style={{ height: "80px" }}>
        <Footer></Footer>
      </div>
    </div>
  );
}
export default App;
