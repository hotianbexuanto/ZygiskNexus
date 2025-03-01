#[derive(Debug)]
pub enum RootImpl {
    Magisk,
    KernelSU,
    None,
    // 删除未使用的 Multiple 变体
}

static mut ROOT_IMPL: RootImpl = RootImpl::None;

pub fn get_root_impl() -> &'static RootImpl {
    // 使用 &raw const 替代不安全的静态可变引用
    unsafe { &raw const ROOT_IMPL }
}

// ... existing code ... 