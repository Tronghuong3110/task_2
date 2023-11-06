import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import Sidebar from './component/Layout/Sidebar';
import Header from './component/Layout/Header';
import Probes from './component/Probes/Probes';
import ProbeDetails from './component/ProbeDetails/ProbeDetails';
import Footer from './component/Layout/Footer';
import Module from './component/Modules/Module';
import ModuleHistory from './component/ModuleHistory/ModuleHistory';
import Dashboard from './component/Dashboard/Dashboard';
import { ProbesProvider } from "./component/Probes/ProbesTable/ProbesContext";
import {Routes,Route} from 'react-router-dom'
function App() {
  const [isHideSideBar, setHideSideBar] = useState(false)
  return (
    <div className="App" style={{ padding: 0, margin: 0, backgroundColor: "#0b0c24"}}>
      <div className='headerContainer' style={{ height: "80px" }}>
        <Header></Header>
      </div>
      <main className='mainContainer d-flex' style={{ backgroundColor: "#0b0c24" }} >
        <div className='sidebarContainer' style={{ width: isHideSideBar==false?"10%":"5%", backgroundColor: "transparent", height: "100%",transition: "width 0.5s" }}>
          <Sidebar isHide={isHideSideBar} setHideSideBar = {setHideSideBar} ></Sidebar>
        </div>
        <div className='dataContainer' style={{ width: isHideSideBar==true?"95%":"90%", backgroundColor: "transparent", padding: "0 30px",transition: "width 0.5s" }} >
          {/* <ProbesProvider>
            <Probes></Probes>
          </ProbesProvider> */}
          {/* <Module></Module> */}
          {/* <ModuleHistory></ModuleHistory> */}
          <Routes>
            <Route path='/' element={<Dashboard></Dashboard>}></Route>
            <Route path='/probe' element={<ProbesProvider><Probes></Probes></ProbesProvider>}></Route>
            <Route path='/details/:id' element={<ProbeDetails></ProbeDetails>}></Route>
            <Route path='/modules' element={<Module></Module>}></Route>
            <Route path='/module_history' element={<ModuleHistory></ModuleHistory>}></Route>
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