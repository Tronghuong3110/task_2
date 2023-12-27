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
import Statistic from './component/Statistic/Statistic'
import { ProbesProvider } from "./component/Probes/ProbesTable/ProbesContext";
import { Routes, Route } from 'react-router-dom'
import RecycleBin from './component/RecycleBin/RecycleBin';
function App() {
  const [isHideSideBar, setHideSideBar] = useState(false)
  return (
    <React.Fragment>

      <div className="App" style={{ padding: 0, margin: 0, backgroundColor: "#0b0c24" }}>
        <div className='headerContainer' style={{ height: "80px" }}>
          <Header></Header>
        </div>
        <main className='mainContainer d-flex' style={{ backgroundColor: "#0b0c24",minHeight:"calc(100vh - 160px)" }} >
          <div className='sidebarContainer' style={{ width: isHideSideBar == false ? "10%" : "5%", backgroundColor: "transparent", height: "100%", transition: "width 0.5s" }}>
            <Sidebar isHide={isHideSideBar} setHideSideBar={setHideSideBar} ></Sidebar>
          </div>
          <div className='dataContainer' style={{ width: isHideSideBar == true ? "95%" : "90%", backgroundColor: "transparent", padding: "0 30px", transition: "width 0.5s" }} >

            <Routes>
              <Route path='/' exact element={<Dashboard></Dashboard>}></Route>
              <Route path='/probe' exact={true} element={<ProbesProvider><Probes></Probes></ProbesProvider>}></Route>
              <Route path='/details/:id' exact={true} element={<ProbeDetails></ProbeDetails>}></Route>
              <Route path='/modules' exact={true} element={<Module></Module>}></Route>
              <Route path='/module_history/:id' exact={true} element={<ModuleHistory></ModuleHistory>}></Route>
              <Route path='/statistic' exact={true} element={<Statistic></Statistic>}></Route>
              <Route path='/bin' exact={true} element={<RecycleBin></RecycleBin>}></Route>
            </Routes>
          </div>
        </main>
        <div className='footerContainer' style={{ height: "80px" }}>
          <Footer></Footer>
        </div>
      </div>
    </React.Fragment>
  );
}
export default App;
