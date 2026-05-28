import { Routes, Route, useNavigate } from 'react-router-dom'
import { CompraPage } from './components/CompraPage'
import { MercadoPagoPage } from './components/MercadoPagoPage'

function App() {
  const navigate = useNavigate()

  return (
    <Routes>
      <Route
        path="/"
        element={
          <CompraPage
            usuarioId="1"
            navigate={navigate}
          />
        }
      />
      <Route path="/pago/simulado" element={<MercadoPagoPage />} />
    </Routes>
  )
}

export default App
